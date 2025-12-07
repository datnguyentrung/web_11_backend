package com.dat.backend_version_2.enums.tournament;

public enum TournamentScope {
    EXCHANGE_EVENT,        // Giao lưu, sự kiện trao đổi (không hẳn là giải chính thức)
    INTERNAL_TOURNAMENT,   // Giải nội bộ (giữa các chi nhánh / trong trung tâm)
    CITY_TOURNAMENT,       // Giải cấp thành phố (do Liên đoàn Taekwondo thành phố tổ chức)
    NATIONAL_TOURNAMENT,   // Giải cấp quốc gia (mở rộng, có nhiều đơn vị tham gia, không hẳn là vô địch)
    NATIONAL_CHAMPIONSHIP, // Giải vô địch quốc tế (trọng điểm, có tính chính thức cao nhất trong nước)
}