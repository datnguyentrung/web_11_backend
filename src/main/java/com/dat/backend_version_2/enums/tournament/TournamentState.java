package com.dat.backend_version_2.enums.tournament;

public enum TournamentState {

    /** Giải đấu đã được lên lịch nhưng chưa bắt đầu. */
    UPCOMING,

    /** Giải đấu đang diễn ra. */
    ONGOING,

    /** Giải đấu đã kết thúc. */
    COMPLETED,

    /** Giải đấu bị hủy bỏ. */
    CANCELLED,
}