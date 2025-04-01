package com.midou.tutorial.Workspace.configuration;



import com.midou.tutorial.Workspace.repositories.WorkspaceInvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class InvitationCleanupTask {

    @Autowired
    private WorkspaceInvitationRepository invitationRepository;

    @Scheduled(cron = "0 0 0 * * *") // Runs daily at midnight
    @Transactional
    public void cleanUpExpiredInvitations() {
        invitationRepository.deleteAll(
                invitationRepository.findAll().stream()
                        .filter(inv -> inv.getExpiresAt().isBefore(LocalDateTime.now()))
                        .toList()
        );
    }
}