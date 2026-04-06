ALTER TABLE user_course_progress
    ADD COLUMN started_at TIMESTAMP DEFAULT now(),
ADD COLUMN finished_at TIMESTAMP;

DROP TABLE user_courses;

CREATE TABLE unit_resources (
                                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                unit_id UUID REFERENCES course_units(id) ON DELETE CASCADE,
                                resource_type VARCHAR(32) NOT NULL CHECK (resource_type IN ('VIDEO','PDF','AUDIO')),
                                url TEXT NOT NULL,
                                title VARCHAR(255),
                                description TEXT,
                                created_at TIMESTAMP DEFAULT now()
);