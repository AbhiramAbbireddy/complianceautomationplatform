CREATE TABLE compliance_documents
(
    id BIGSERIAL PRIMARY KEY,

    file_name VARCHAR(255) NOT NULL,

    file_type VARCHAR(100),

    file_size BIGINT,

    s3_key VARCHAR(500),

    document_url VARCHAR(1000),

    uploaded_at TIMESTAMP,

    compliance_id BIGINT NOT NULL,

    uploaded_by BIGINT NOT NULL,

    CONSTRAINT fk_document_compliance
        FOREIGN KEY(compliance_id)
        REFERENCES compliances(id),

    CONSTRAINT fk_document_user
        FOREIGN KEY(uploaded_by)
        REFERENCES users(id)
);