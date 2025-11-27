package com.burakpozut.common.api;

import java.time.LocalDateTime;

record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message) {

}
