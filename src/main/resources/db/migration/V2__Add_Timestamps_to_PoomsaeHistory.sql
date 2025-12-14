-- V2__Add_Timestamps_to_PoomsaeHistory.sql

-- Bổ sung cột created_at (thời gian tạo, không được null)
ALTER TABLE tournament.poomsae_history
    ADD COLUMN created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW();

-- Bổ sung cột updated_at (thời gian cập nhật, có thể null hoặc không null tùy nhu cầu)
ALTER TABLE tournament.poomsae_history
    ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE;