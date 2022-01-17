package com.softjourn.common.audit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.Entity;
import javax.persistence.Id;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityContextHolder.class, AuditEntityListener.class})
public class AuditEntityListenerTest {

    private SecurityContext securityContext;

    private Authentication authentication;

    private AuditRepository auditRepository;

    private AuditEntityListener entityListener;

    private TestEntity entity = new TestEntity(TEST_ENTITY_ID);
    private OtherTestEntity otherTestEntity = new OtherTestEntity(OTHER_TEST_ENTITY_ID);

    private static final String TEST_ACCOUNT = "TEST";
    private static final Long TEST_ENTITY_ID = 10L;
    private static final String OTHER_TEST_ENTITY_ID = "sdfgq345df";

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SecurityContextHolder.class);

        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(TEST_ACCOUNT);

        securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(SecurityContextHolder.getContext()).thenAnswer((Answer<SecurityContext>) invocation -> securityContext );

        auditRepository = mock(AuditRepository.class);

        entityListener = new AuditEntityListener();
        AuditEntityListener.setRepository(auditRepository);
    }

    @Test
    public void postCreate() throws Exception {
        entityListener.postCreate(entity);

        Mockito.verify(auditRepository).save(argThat(auditEntity -> {
            assertNull(auditEntity.getId());
            assertEquals(TEST_ENTITY_ID.toString(), auditEntity.getEntityId());
            assertEquals("TestEntity", auditEntity.getEntityName());
            assertEquals(TEST_ACCOUNT, auditEntity.getUserName());
            assertEquals(AuditEntity.Action.CREATE, auditEntity.getAction());
            assertNotNull(auditEntity.getDate());
            return true;
        }));
    }


    @Test
    public void postUpdate() throws Exception {
        entityListener.postUpdate(entity);

        Mockito.verify(auditRepository).save(argThat(auditEntity -> {
            assertNull(auditEntity.getId());
            assertEquals(TEST_ENTITY_ID.toString(), auditEntity.getEntityId());
            assertEquals("TestEntity", auditEntity.getEntityName());
            assertEquals(TEST_ACCOUNT, auditEntity.getUserName());
            assertEquals(AuditEntity.Action.UPDATE, auditEntity.getAction());
            assertNotNull(auditEntity.getDate());
            return true;
        }));
    }


    @Test
    public void postRemove() throws Exception {
        entityListener.postRemove(entity);

        Mockito.verify(auditRepository).save(argThat(auditEntity -> {
            assertNull(auditEntity.getId());
            assertEquals(TEST_ENTITY_ID.toString(), auditEntity.getEntityId());
            assertEquals("TestEntity", auditEntity.getEntityName());
            assertEquals(TEST_ACCOUNT, auditEntity.getUserName());
            assertEquals(AuditEntity.Action.REMOVE, auditEntity.getAction());
            assertNotNull(auditEntity.getDate());
            return true;
        }));
    }


    @Test
    public void getAuditEntity() throws Exception {
        AuditEntity auditEntity = entityListener.getAuditEntity(entity, AuditEntity.Action.CREATE);

        assertNull(auditEntity.getId());
        assertEquals(TEST_ENTITY_ID.toString(), auditEntity.getEntityId());
        assertEquals("TestEntity", auditEntity.getEntityName());
        assertEquals(TEST_ACCOUNT, auditEntity.getUserName());
        assertEquals(AuditEntity.Action.CREATE, auditEntity.getAction());
        assertNotNull(auditEntity.getDate());
    }

    @Test
    public void getAuditOtherEntity() throws Exception {
        AuditEntity auditEntity = entityListener.getAuditEntity(otherTestEntity, AuditEntity.Action.REMOVE);

        assertNull(auditEntity.getId());
        assertEquals(OTHER_TEST_ENTITY_ID, auditEntity.getEntityId());
        assertEquals("OtherTestEntity", auditEntity.getEntityName());
        assertEquals(TEST_ACCOUNT, auditEntity.getUserName());
        assertEquals(AuditEntity.Action.REMOVE, auditEntity.getAction());
        assertNotNull(auditEntity.getDate());
    }

    @Entity
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestEntity {
        @Id
        private Long id;

    }

    @Entity
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OtherTestEntity {
        @Id
        private String id;

    }

}
