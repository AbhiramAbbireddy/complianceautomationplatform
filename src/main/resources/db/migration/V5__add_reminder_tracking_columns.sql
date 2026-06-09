ALTER TABLE compliance_assignments
ADD COLUMN reminder_3day_sent BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE compliance_assignments
ADD COLUMN reminder_1day_sent BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE compliance_assignments
ADD COLUMN overdue_reminder_sent BOOLEAN NOT NULL DEFAULT FALSE;