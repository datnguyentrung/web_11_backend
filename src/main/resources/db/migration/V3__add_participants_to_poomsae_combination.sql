-- Bước 1: Thêm cột với DEFAULT 0 và ép NOT NULL ngay lập tức
ALTER TABLE tournament.poomsae_combination
    ADD COLUMN participants INTEGER NOT NULL DEFAULT 0;

-- Bước 2: (Phòng xa) Cập nhật lại các bản ghi cũ nếu có lỗi xảy ra trong quá trình alter
UPDATE tournament.poomsae_combination
    SET participants = 0
    WHERE participants IS NULL;