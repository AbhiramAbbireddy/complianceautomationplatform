package com.abhiram.complianceautomationplatform.notification.template;

public class EmailTemplates {
    private static String layout(
            String title,
            String content) {

        return """
                <html>
                <body style="
                    font-family: Arial, sans-serif;
                    background-color:#f5f5f5;
                    padding:20px;">

                    <div style="
                        max-width:600px;
                        margin:auto;
                        background:white;
                        padding:30px;
                        border-radius:10px;
                        box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                        <h2 style="color:#2563eb;">
                            Compliance Automation Platform
                        </h2>

                        <h3>%s</h3>

                        %s

                        <hr>

                        <p style="color:gray;">
                            Compliance Automation Platform
                        </p>

                    </div>

                </body>
                </html>
                """
                .formatted(title, content);
    }

    public static String assignmentTemplate(
            String employeeName,
            String complianceTitle,
            String assignedBy) {

        String content = """
                <p>Hello %s,</p>

                <p>
                A new compliance has been assigned to you.
                </p>

                <table>
                    <tr>
                        <td><b>Compliance:</b></td>
                        <td>%s</td>
                    </tr>

                    <tr>
                        <td><b>Assigned By:</b></td>
                        <td>%s</td>
                    </tr>
                </table>

                <br>

                <p>
                Please complete it before the due date.
                </p>
                """
                .formatted(
                        employeeName,
                        complianceTitle,
                        assignedBy);

        return layout(
                "New Compliance Assigned",
                content);
    }

    public static String completionTemplate(
            String managerName,
            String complianceTitle,
            String employeeName) {

        String content = """
                <p>Hello %s,</p>

                <p>
                %s has completed the compliance.
                </p>

                <table>
                    <tr>
                        <td><b>Compliance:</b></td>
                        <td>%s</td>
                    </tr>
                </table>

                <br>

                <p>
                Please review and verify it.
                </p>
                """
                .formatted(
                        managerName,
                        employeeName,
                        complianceTitle);

        return layout(
                "Compliance Completed",
                content);
    }

    public static String verificationTemplate(
            String employeeName,
            String complianceTitle) {

        String content = """
                <p>Hello %s,</p>

                <p>
                Your compliance has been verified successfully.
                </p>

                <table>
                    <tr>
                        <td><b>Compliance:</b></td>
                        <td>%s</td>
                    </tr>
                </table>

                <br>

                <p>
                Great work!
                </p>
                """
                .formatted(
                        employeeName,
                        complianceTitle);

        return layout(
                "Compliance Verified",
                content);
    }

    public static String reminderTemplate(
            String employeeName,
            String complianceTitle,
            long daysLeft) {

        String content = """
                <p>Hello %s,</p>

                <p>
                This is a reminder that your compliance is due soon.
                </p>

                <table>
                    <tr>
                        <td><b>Compliance:</b></td>
                        <td>%s</td>
                    </tr>

                    <tr>
                        <td><b>Days Remaining:</b></td>
                        <td>%d</td>
                    </tr>
                </table>

                <br>

                <p>
                Please complete it before the deadline.
                </p>
                """
                .formatted(
                        employeeName,
                        complianceTitle,
                        daysLeft);

        return layout(
                "Compliance Reminder",
                content);
    }

    public static String overdueTemplate(
            String employeeName,
            String complianceTitle) {

        String content = """
                <p>Hello %s,</p>

                <p style="color:red;">
                Your compliance is overdue.
                Immediate action is required.
                </p>

                <table>
                    <tr>
                        <td><b>Compliance:</b></td>
                        <td>%s</td>
                    </tr>
                </table>

                <br>

                <p>
                Please complete it as soon as possible.
                </p>
                """
                .formatted(
                        employeeName,
                        complianceTitle);

        return layout(
                "Compliance Overdue",
                content);
    }

}
