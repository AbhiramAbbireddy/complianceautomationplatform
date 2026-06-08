ALTER TABLE compliances
DROP CONSTRAINT IF EXISTS compliances_status_check;

ALTER TABLE compliances
ADD CONSTRAINT compliances_status_check
CHECK (
    status IN (
        'PENDING',
        'ASSIGNED',
        'IN_PROGRESS',
        'COMPLETED',
        'OVERDUE',
        'VERIFIED'
    )
);