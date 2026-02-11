package org.example.constant;

public class MessageConstant {

    public static final String LOGIN_EMPTY_FIELDS = "Vui lòng điền đầy đủ thông tin!";
    public static final String LOGIN_FAIL = "Tên đăng nhập hoặc mật khẩu không đúng!";
    public static final String LOGIN_SUCCESS = "Đăng nhập thành công!";

    public static final String REGISTER_EMPTY_FIELDS = "Vui lòng điền đầy đủ tất cả các trường!";
    public static final String REGISTER_EMAIL_INVALID = "Định dạng email không hợp lệ!";
    public static final String REGISTER_USERNAME_SHORT = "Tên đăng nhập phải có ít nhất 5 ký tự!";
    public static final String REGISTER_PASSWORD_INVALID = "Mật khẩu phải từ 6-20 ký tự, gồm chữ hoa, thường, số và ký tự đặc biệt!";
    public static final String REGISTER_PASSWORD_MISMATCH = "Mật khẩu xác nhận không khớp!";
    public static final String REGISTER_EMAIL_EXIST = "Email đã được đăng ký!";
    public static final String REGISTER_USERNAME_EXIST = "Tên đăng nhập đã tồn tại!";
    public static final String REGISTER_SUCCESS = "SUCCESS";
    public static final String REGISTER_FAIL = "Lỗi hệ thống: Đăng ký thất bại.";

    public static final String FORGOT_PASS_EMAIL_EMPTY = "Vui lòng nhập địa chỉ Email!";
    public static final String FORGOT_PASS_EMAIL_NOT_EXIST = "Email này chưa được đăng ký trong hệ thống!";
    public static final String FORGOT_PASS_SEND_FAIL = "Gửi mã thất bại. Vui lòng kiểm tra kết nối mạng!";
    public static final String OTP_SENT_SUCCESS = "Đã gửi lại mã OTP vào email!";
    public static final String OTP_EMPTY = "Vui lòng nhập mã OTP";
    public static final String AGREE_CHECK_BOX = "Vui lòng đồng ý với các điều khoản sử dụng!";
    public static final String OTP_RESEND_FAIL = "Gửi lại thất bại. Vui lòng thử lại sau.";
    public static final String OTP_INVALID = "Mã OTP không chính xác.";
    public static final String OTP_EXPIRED = "Mã OTP đã hết hạn. Vui Lòng gửi lại!";

    public static final String VALIDATION_NAME_EMPTY = "Tên không được để trống!";
    public static final String VALIDATION_NAME_SHORT = "Tên quá ngắn!";
    public static final String VALIDATION_PHONE_INVALID = "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)!";
    public static final String CHANGE_PASS_OLD_WRONG = "Mật khẩu hiện tại không chính xác!";
    public static final String CHANGE_PASS_SUCCESS = "Đổi mật khẩu thành công!";
    public static final String CHANGE_NAME_SUCCESS = "Đổi tên thành công!";
    public static final String ACCOUNT_DELETE_FAIL = "Lỗi hệ thống: Không thể xóa tài khoản.";
    public static final String UPDATE_SUCCESS = "Cập nhật thành công!";
    public static final String UPDATE_FAIL = "Lỗi hệ thống: Không thể lưu thông tin. Vui lòng thử lại!";

    public static final String TITLE_LOGIN = "Đăng nhập";
    public static final String TITLE_REGISTER = "Đăng ký tài khoản";
    public static final String TITLE_HOME = "Trang chủ";
    public static final String TITLE_HISTORY = "Lịch sử";
    public static final String TITLE_FAVORITE = "Yêu thích";
    public static final String TITLE_ACCOUNT = "Tài khoản cá nhân";
    public static final String TITLE_CONFIRM_OTP = "Xác nhận OTP";
    public static final String TITLE_FORGOT_PASS = "Quên mật khẩu";
    public static final String TITLE_CONFIRM_INFO = "Xác nhận thông tin";

    public static final String FAVORITE_EMPTY = "Bạn chưa yêu thích bộ truyện nào.";
    public static final String FAVORITE_LOGIN_REQ = "Vui lòng đăng nhập để xem danh sách yêu thích.";
    public static final String HISTORY_EMPTY = "Chưa có lịch sử đọc truyện.";
    public static final String HISTORY_LOGIN_REQ = "Vui lòng đăng nhập để xem lịch sử.";
    public static final String SEARCH_NO_RESULT = "Không tìm thấy truyện nào.";
    public static final String LIST_END_DATA = "Không còn dữ liệu hoặc đã hết trang.";

    public static final String ERR_NETWORK = "Lỗi kết nối mạng hoặc API!";
    public static final String ERR_SYSTEM = "Đã xảy ra lỗi không mong muốn!";
    public static final String ERR_DB_CONNECT = "Lỗi kết nối CSDL, vui lòng thử lại!";

    public static final String TITLE_VIEW_ALL_NEW = "Danh sách truyện - Mới cập nhật";
    public static final String TITLE_VIEW_ALL_COMPLETED = "Danh sách truyện - Đã hoàn thành";
    public static final String TITLE_VIEW_ALL_COMING = "Danh sách truyện - Sắp ra mắt";
    public static final String TITLE_ALL_CATEGORIES = "Tất cả thể loại";

    public static final String ACCOUNT_LOCKED = "Tài khoản của bạn đã bị khóa!";

    public static final String ERR_API_EMPTY = "Dữ liệu từ máy chủ rỗng.";
    public static final String ERR_API_FORMAT = "Dữ liệu từ máy chủ không đúng định dạng.";

    public static final String ERR_DB_QUERY = "Lỗi truy vấn dữ liệu.";
    public static final String ERR_DB_SAVE = "Không thể lưu dữ liệu vào hệ thống.";
    public static final String ERR_DB_DELETE = "Không thể xóa dữ liệu.";
    public static final String ERR_DB_UPDATE = "Cập nhật dữ liệu thất bại.";
    public static final String ERR_USER_NOT_FOUND = "Không tìm thấy thông tin người dùng.";

    public static final String ERR_LOAD_CATEGORIES = "Không thể tải danh sách thể loại.";
    public static final String ERR_LOAD_BOOKS = "Không thể tải danh sách truyện.";
    public static final String ERR_BOOK_NOT_FOUND = "Không tìm thấy thông tin truyện hoặc truyện bị lỗi.";
    public static final String ERR_CHAPTER_CONTENT = "Nội dung chương bị lỗi hoặc không tồn tại.";
    public static final String ERR_SEARCH = "Lỗi khi tìm kiếm truyện.";

    public static final String MSG_LOADING = "Đang tải dữ liệu...";
    public static final String MSG_EMPTY_DATA = "Không có dữ liệu.";

    public static final String CHAT_USER_TITLE = "Bạn";
    public static final String CHAT_AI_TITLE = "Gemini AI";
    public static final String CHAT_INPUT_EMPTY = "Vui lòng nhập nội dung tin nhắn!";
    public static final String CHAT_ERR_NO_RESPONSE = "Không nhận được phản hồi từ AI.";
    public static final String CHAT_ERR_API = "Lỗi khi gọi API AI: ";

    public static final String GEMINI_API_KEY = "AIzaSyA46sQ6nKF-CC6kFyro-ols8LfwMTRimrk";
    public static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";
}