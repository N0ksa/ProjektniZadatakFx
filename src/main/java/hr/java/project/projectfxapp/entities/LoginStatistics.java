package hr.java.project.projectfxapp.entities;

import javafx.scene.chart.XYChart;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public record LoginStatistics(String username, String associatedMathClub
        , LocalDateTime loginTime, LocalDateTime logoutTime, Integer loginDuration) implements Serializable {
    public static LoginStatistics getStatistic(User user, MathClub mathClub, LocalDateTime loginTime, LocalDateTime logoutTime) {
        Integer loginDuration = 0;

        if (Optional.ofNullable(loginTime).isPresent() && Optional.ofNullable(logoutTime).isPresent()) {
            loginDuration = calculateLoginDuration(loginTime, logoutTime);
        }

        String username = user.getUsername();
        String associatedMathClub = mathClub.getName();

        return new LoginStatistics(username, associatedMathClub,loginTime, logoutTime, loginDuration);
    }

    private static Integer calculateLoginDuration(LocalDateTime loginTime, LocalDateTime logoutTime) {
        long seconds = java.time.Duration.between(loginTime, logoutTime).getSeconds();
        return Math.toIntExact(seconds);
    }


}
