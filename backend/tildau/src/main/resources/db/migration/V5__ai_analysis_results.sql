DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'exercise_status') THEN
CREATE TYPE exercise_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED');
END IF;
END$$;

ALTER TABLE user_exercises
    ADD COLUMN status exercise_status NOT NULL DEFAULT 'PENDING';

ALTER TABLE user_exercises
DROP COLUMN IF EXISTS score,
    DROP COLUMN IF EXISTS feedback;


CREATE TABLE ai_analysis_results (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                                     user_exercise_id UUID NOT NULL
                                         REFERENCES user_exercises(id) ON DELETE CASCADE,

                                     pronunciation_score INTEGER,
                                     fluency_score INTEGER,
                                     embedding_score INTEGER,
                                     overall_score INTEGER,

                                     flags JSONB,
                                     feedback JSONB,
                                     metrics JSONB,

                                     model_version TEXT,

                                     created_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_ai_analysis_user_exercise
    ON ai_analysis_results(user_exercise_id);

CREATE INDEX idx_user_exercises_user_id
    ON user_exercises(user_id);

CREATE INDEX idx_user_exercises_exercise_id
    ON user_exercises(exercise_id);