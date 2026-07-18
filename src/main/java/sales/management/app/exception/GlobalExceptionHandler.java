package sales.management.app.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Bắt lỗi gõ sai URL hoặc tài nguyên không tồn tại
    @ExceptionHandler(NoResourceFoundException.class)
    public Object handle404(NoResourceFoundException ex, HttpServletRequest request) {

        // Kiểm tra xem request này có phải từ các đường dẫn hệ thống ngầm, API hoặc
        // công cụ dev không
        String uri = request.getRequestURI();
        if (uri.startsWith("/.well-known") || uri.startsWith("/api") || uri.endsWith(".json")) {
            // Trả về JSON lỗi tiêu chuẩn, giúp các công cụ hệ thống không bị lỗi render
            // HTML
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", ex.getMessage()));
        }

        // Nếu là người dùng bình thường gõ sai URL trên thanh địa chỉ, hiển thị giao
        // diện HTML 404
        return "error-404";
    }

    // Bắt tất cả các lỗi logic phát sinh khác (Lỗi 500)
    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, HttpServletRequest request) {
        System.err.println("Hệ thống gặp lỗi logic tại: " + request.getRequestURI() + " -> " + ex.getMessage());

        // Tương tự, nếu lỗi xảy ra ở API thì trả về JSON thay vì trả về giao diện HTML
        if (request.getRequestURI().startsWith("/api")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "Đã xảy ra lỗi hệ thống"));
        }

        return "error-404";
    }

}
