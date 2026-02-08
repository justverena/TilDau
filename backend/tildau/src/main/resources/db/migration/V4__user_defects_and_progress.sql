CREATE TABLE user_defect_types (
                                   user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                                   defect_type_id INTEGER NOT NULL REFERENCES defect_types(id)
);

CREATE TABLE user_courses (
                              user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,

                              started_at TIMESTAMP DEFAULT now(),
                              finished_at TIMESTAMP,

                              PRIMARY KEY (user_id, course_id)
);

CREATE TABLE user_exercises (
                                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                                exercise_id UUID REFERENCES exercises(id) ON DELETE CASCADE,
                                attempt_number INTEGER DEFAULT 1,
                                audio_url TEXT NOT NULL,
                                score NUMERIC(5,2),
                                feedback TEXT,
                                completed_at TIMESTAMP DEFAULT now()
);

CREATE TABLE user_unit_progress (
                                    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                    unit_id UUID NOT NULL REFERENCES course_units(id) ON DELETE CASCADE,
                                    completed_exercises INTEGER DEFAULT 0,
                                    total_exercises INTEGER NOT NULL,
                                    is_completed BOOLEAN DEFAULT false,
                                    PRIMARY KEY (user_id, unit_id)
);

CREATE TABLE user_course_progress (
                                      user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                      course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
                                      completed_units INTEGER DEFAULT 0,
                                      total_units INTEGER NOT NULL,
                                      progress_percent NUMERIC(5,2) DEFAULT 0,
                                      PRIMARY KEY (user_id, course_id)
);
