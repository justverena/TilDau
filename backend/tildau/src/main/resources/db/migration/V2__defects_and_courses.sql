CREATE TABLE defect_types (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO defect_types (id, name) VALUES (1, 'articulation'), (2, 'stuttering'), (3, 'post_stroke');

CREATE TABLE courses (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         defect_type_id INTEGER REFERENCES defect_types(id),
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         is_active BOOLEAN DEFAULT true,
                         created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE course_units (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              course_id UUID REFERENCES courses(id) ON DELETE CASCADE,
                              title VARCHAR(255) NOT NULL,
                              description TEXT,
                              order_index INTEGER NOT NULL,
                              created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE exercises (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           unit_id UUID REFERENCES course_units(id) ON DELETE CASCADE,
                           exercise_type VARCHAR(32) NOT NULL,
    -- READ_ALOUD | REPEAT_AFTER_AUDIO
                           title VARCHAR(255),
                           instruction TEXT,
                           expected_text TEXT NOT NULL,
                           reference_audio_url TEXT,
    -- only for REPEAT_AFTER_AUDIO
                           order_index INTEGER NOT NULL,
                           created_at TIMESTAMP DEFAULT now()
);