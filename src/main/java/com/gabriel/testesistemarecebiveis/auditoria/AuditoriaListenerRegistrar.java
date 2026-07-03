package com.gabriel.testesistemarecebiveis.auditoria;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.springframework.stereotype.Component;

/**
 * Registra o {@link AuditoriaEventListener} na {@link EventListenerRegistry} do
 * Hibernate apos a inicializacao do contexto.
 *
 * <p>O listener e um bean gerenciado pelo Spring (com suas dependencias
 * injetadas); aqui apenas anexamos a mesma instancia aos eventos de
 * pos-persistencia do Hibernate, tornando a auditoria transversal a todas as
 * entidades.
 */
@Component
public class AuditoriaListenerRegistrar {

    private final EntityManagerFactory entityManagerFactory;
    private final AuditoriaEventListener auditoriaEventListener;

    public AuditoriaListenerRegistrar(
            EntityManagerFactory entityManagerFactory,
            AuditoriaEventListener auditoriaEventListener) {
        this.entityManagerFactory = entityManagerFactory;
        this.auditoriaEventListener = auditoriaEventListener;
    }

    @PostConstruct
    public void registrar() {
        SessionFactoryImplementor sessionFactory = entityManagerFactory
                .unwrap(SessionFactoryImplementor.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry()
                .requireService(EventListenerRegistry.class);

        registry.appendListeners(
                EventType.POST_INSERT, auditoriaEventListener);
        registry.appendListeners(
                EventType.POST_UPDATE, auditoriaEventListener);
        registry.appendListeners(
                EventType.POST_DELETE, auditoriaEventListener);
    }
}
