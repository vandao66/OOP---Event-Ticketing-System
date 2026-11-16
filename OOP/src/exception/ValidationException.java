package exception;
/*Sử dụng khi dữ liệu hoặc thao 
tác không đáp ứng quy tắc nghiệp vụ, ví dụ đặt 0 vé hoặc hủy vé đã check-in*/
public class ValidationException extends AppException {
    public ValidationException(String message) {
        super(message);
    }
}