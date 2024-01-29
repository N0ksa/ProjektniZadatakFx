package hr.java.project.projectfxapp.entities;

import hr.java.project.projectfxapp.utility.manager.SessionManager;

public sealed interface LoginStatisticsRecorder permits SessionManager {
    LoginStatistics recordLoginStatistics();
}
