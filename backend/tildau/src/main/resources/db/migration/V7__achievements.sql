CREATE TABLE achievements (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              code VARCHAR(50) UNIQUE NOT NULL,
                              title VARCHAR(255),
                              description TEXT,
                              created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE user_achievements (
                                   user_id UUID REFERENCES users(id),
                                   achievement_id UUID REFERENCES achievements(id),
                                   unlocked_at TIMESTAMP DEFAULT now(),
                                   PRIMARY KEY (user_id, achievement_id)
);