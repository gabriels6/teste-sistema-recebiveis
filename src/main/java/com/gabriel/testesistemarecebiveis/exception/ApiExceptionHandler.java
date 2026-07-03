package com.gabriel.testesistemarecebiveis.exception;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * Tratamento global de erros da API. Converte excecoes em respostas
 * {@link ApiResponse} padronizadas, deixando explicito se o erro foi do cliente
 * (4xx — dado ausente/invalido, autenticacao/autorizacao) ou do servidor (5xx).
 *
 * <p>Erros ocorridos na cadeia de filtros de seguranca (401/403) sao tratados
 * fora daqui, nos handlers registrados em {@code SecurityConfig}, pois nao
 * chegam ao {@code @RestControllerAdvice}. Ambos reusam {@link ApiErro} e o
 * mesmo formato de resposta.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOG =
            LoggerFactory.getLogger(ApiExceptionHandler.class);

    // --- Erros do cliente (4xx) -------------------------------------------

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(
            BusinessException ex) {
        return build(ApiErro.DADOS_INVALIDOS, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        return build(ApiErro.DADOS_INVALIDOS,
                "Alguns campos sao invalidos.", detalhes);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadable(
            HttpMessageNotReadableException ex) {
        return build(ApiErro.REQUISICAO_MALFORMADA,
                "O corpo da requisicao esta ausente ou malformado.", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        String mensagem = "O parametro '" + ex.getName()
                + "' recebeu um valor invalido: '" + ex.getValue() + "'.";
        return build(ApiErro.REQUISICAO_MALFORMADA, mensagem, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException ex) {
        String mensagem = "O parametro obrigatorio '" + ex.getParameterName()
                + "' nao foi informado.";
        return build(ApiErro.REQUISICAO_MALFORMADA, mensagem, null);
    }

    // --- Autenticacao (4xx) -----------------------------------------------

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(
            InvalidCredentialsException ex) {
        return build(ApiErro.CREDENCIAIS_INVALIDAS, ex.getMessage(),
                List.of("remainingAttempts=" + ex.getRemainingAttempts()));
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserBlocked(
            UserBlockedException ex) {
        return build(ApiErro.USUARIO_BLOQUEADO, ex.getMessage(),
                List.of("remainingAttempts=" + ex.getRemainingAttempts()));
    }

    // --- Recurso / metodo / conflito (4xx) --------------------------------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException ex) {
        return build(ApiErro.RECURSO_NAO_ENCONTRADO, ex.getMessage(), null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResource(
            NoResourceFoundException ex) {
        return build(ApiErro.RECURSO_NAO_ENCONTRADO,
                "Recurso nao encontrado.", null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {
        return build(ApiErro.METODO_NAO_PERMITIDO,
                "Metodo HTTP '" + ex.getMethod()
                        + "' nao suportado para este recurso.", null);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLocking(
            OptimisticLockingFailureException ex) {
        return build(ApiErro.CONFLITO,
                "O registro foi modificado por outra operacao concorrente. "
                        + "Recarregue os dados e tente novamente.", null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(
            DataIntegrityViolationException ex) {
        LOG.warn("Violacao de integridade de dados", ex);
        return build(ApiErro.CONFLITO,
                "A operacao viola uma restricao de integridade dos dados.",
                null);
    }

    // --- Erro do servidor (5xx) -------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        // Loga o detalhe internamente, mas nao o expoe ao cliente.
        LOG.error("Erro interno nao tratado", ex);
        return build(ApiErro.ERRO_INTERNO,
                "Ocorreu um erro interno. Tente novamente mais tarde.", null);
    }

    private ResponseEntity<ApiResponse<Void>> build(
            ApiErro erro, String mensagem, List<String> detalhes) {
        return ResponseEntity.status(erro.status())
                .body(ApiResponse.error(
                        erro.codigo(), erro.name(), mensagem, detalhes));
    }
}
