# WOWTruyen

Đây là một ứng dụng đọc truyện tranh trực tuyến cho phép bạn đọc và tìm kiếm những bộ truyện yêu thích. Ứng dụng giúp bạn sắp xếp, theo dõi các bộ truyện đang đọc, đánh dấu các bộ truyện yêu thích và thậm chí có thể tương tác với một trợ lý AI được tích hợp sẵn.

## Cài đặt

### Yêu cầu

- Java 17+
- JavaFX 17+
- Maven
- MySQL Server
- IDE: IntelliJ IDEA, Eclipse, hoặc Netbeans.

### Cách chạy dự án

- Clone (tải về) dự án.
- Thiết lập cơ sở dữ liệu: Tạo một database MySQL có tên là `WOWTruyen` và chạy đoạn script SQL được cung cấp để tạo các bảng cần thiết.
- Cập nhật thông tin đăng nhập database trong file `DBConnect.java` và cấu hình email (để gửi mã OTP) trong file `EmailUtils.java`.
- Cài đặt các thư viện Maven cần thiết.
- Chạy dự án từ class `Launcher.java` hoặc `Main.java`.

## Hướng dẫn sử dụng

1. Đảm bảo thiết bị của bạn đã được kết nối Internet.
2. Chạy dự án ở class main.
3. Nếu chưa có tài khoản, hãy tạo một tài khoản mới (hỗ trợ xác minh mã OTP qua email).
4. Đăng nhập bằng tài khoản của bạn.
5. Khôi phục mật khẩu qua email nếu bạn lỡ quên.

<p>
    <img src="demo/demo_user/1.png" width="250">
    <img src="demo/demo_user/3.png" width="250">
    <img src="demo/demo_user/2.png" width="250">
    <img src="demo/demo_user/3.5.png" width="250">
</p>

### Màn hình chính (Home Screen)

- Bạn có thể khám phá các bộ truyện ngay từ màn hình chính.
  - Xem danh sách Truyện Mới Cập Nhật, Truyện Đã Hoàn Thành, và Truyện Sắp Ra Mắt.
  - Lọc và duyệt truyện theo từng thể loại.
- Có thể tìm kiếm truyện nhanh chóng thông qua thanh tìm kiếm.
- Bạn có thể xem thêm nhiều truyện hoặc thể loại hơn bằng cách nhấn vào các nút "Xem thêm".

<p>
    <img src="demo/demo_user/4.png" width="250">
    <img src="demo/demo_user/5.png" width="250">
</p>

### Xem thêm (View More)

<p>
    <img src="demo/demo_user/13.png" width="250">
    <img src="demo/demo_user/14.png" width="250">
    <img src="demo/demo_user/15.png" width="250">
    <img src="demo/demo_user/16.png" width="250">
</p>

### Tìm kiếm truyện (Search Book)

<p>
    <img src="demo/demo_user/17.png" width="250">
</p>

### Lịch sử & Yêu thích (History & Favorite Screen)

- **Lịch sử:** Nơi hiển thị danh sách các truyện bạn đã đọc gần đây. Ứng dụng sẽ tự động lưu lại chương mới nhất mà bạn đang đọc dở.
- **Yêu thích:** Bạn có thể lưu và quản lý danh sách các bộ truyện mà mình đã đánh dấu (bookmark).

<p>
    <img src="demo/demo_user/6.png" width="250">
    <img src="demo/demo_user/7.png" width="250">
</p>

### Màn hình đọc truyện (Reading Screen)

- Xem thông tin chi tiết về bộ truyện (tác giả, tình trạng, thể loại, danh sách các chương).
- Trải nghiệm đọc truyện với giao diện tối ưu, cuộn trang mượt mà và thao tác chuyển chương (Chương sau/Chương trước) cực kỳ nhanh chóng.

<p>
    <img src="demo/demo_user/18.png" width="250">
    <img src="demo/demo_user/19.png" width="250">
    <img src="demo/demo_user/20.png" width="250">
</p>

### ChatBox AI

- **ChatBox AI:** Trò chuyện và tương tác với trợ lý ảo Gemini AI để nhận các đề xuất truyện hay hoặc giải đáp các thắc mắc chung.
<p>
    <img src="demo/demo_user/21.png" width="250">
</p>

### Màn hình Tài khoản (Account Screen)

- Xem thông tin cá nhân của bạn, bao gồm ảnh đại diện (avatar), họ và tên, và địa chỉ email.
- Quản lý và cập nhật hồ sơ cá nhân:
  - **Đổi ảnh đại diện (Change Avatar):** Tải lên một bức ảnh mới từ thiết bị để làm ảnh đại diện.
  - **Đổi tên (Change Name):** Cập nhật tên hiển thị của bạn.
  - **Đổi mật khẩu (Change Password):** Cập nhật mật khẩu tài khoản một cách an toàn.
  - **Xóa tài khoản (Delete Account):** Xóa dữ liệu và thông tin tài khoản của bạn.
- Bạn có thể đăng xuất an toàn khỏi phiên làm việc hiện tại.

<p>
    <img src="demo/demo_user/8.png" width="250">
    <img src="demo/demo_user/9.png" width="250">
    <img src="demo/demo_user/10.png" width="250">
    <img src="demo/demo_user/11.png" width="250">
    <img src="demo/demo_user/12.png" width="250">
</p>

### Bảng điều khiển dành cho Quản trị viên (Admin Dashboard)

- **Giới hạn quyền truy cập:** Khu vực này chỉ dành riêng cho các tài khoản có vai trò `ADMIN`.
- **Quản lý người dùng:** Bạn có thể xem danh sách tất cả người dùng đã đăng ký, tìm kiếm người dùng cụ thể bằng ID, tên đăng nhập hoặc email, cũng như dễ dàng khóa (ban) hoặc mở khóa tài khoản để duy trì nguyên tắc cộng đồng.
- **Quản lý truyện:** Bạn có thể duyệt và tìm kiếm các bộ truyện được lấy về từ API. Admin có thẩm quyền ẩn các bộ truyện không phù hợp khỏi hệ thống, đảm bảo người dùng thường sẽ không còn nhìn thấy chúng.
- **Thống kê hệ thống:** Theo dõi các chỉ số tổng quan của nền tảng qua các biểu đồ trực quan, bao gồm biểu đồ cột hiển thị tốc độ tăng trưởng người dùng theo thời gian và biểu đồ tròn thể hiện sự phân bổ của các thể loại truyện.

<p>
    <img src="demo/demo_admin/1.png" width="250">
    <img src="demo/demo_admin/2.png" width="250">
    <img src="demo/demo_admin/3.png" width="250">
    <img src="demo/demo_admin/4.png" width="250">
    <img src="demo/demo_admin/5.png" width="250">
    <img src="demo/demo_admin/6.png" width="250">
    <img src="demo/demo_admin/7.png" width="250">
    <img src="demo/demo_admin/8.png" width="250">
    <img src="demo/demo_admin/9.png" width="250">
    <img src="demo/demo_admin/10.png" width="250">
</p>

## Công nghệ & Thư viện

### Giao diện & Kiến trúc (UI & Architecture)

- **JavaFX** – Sử dụng để xây dựng giao diện người dùng đồ họa (GUI) cho ứng dụng desktop.
- **Mô hình MVC** – Chia tách ứng dụng thành ba thành phần riêng biệt: Model, View, và Controller.

### Tích hợp API (API Integration)

- **OkHttp3** – Một HTTP client đồng bộ và bất đồng bộ dùng để gọi OtruyenAPI và Gemini API.
- **Gson** – Thư viện dùng để chuyển đổi dữ liệu JSON trả về từ API thành các đối tượng Java.

### Cơ sở dữ liệu (Database)

- **MySQL Connector/J** – Driver JDBC để kết nối với cơ sở dữ liệu MySQL.

### Xử lý Email

- **Jakarta Mail / Angus Mail** – Các thư viện dùng để gửi email xác thực mã OTP và khôi phục mật khẩu.

### Bảo mật & Xác thực

- **JBCrypt** – Thư viện dùng để băm (hash) mật khẩu và xác thực người dùng một cách an toàn.

### Đồng thời & Đa luồng (Concurrency & Multi-threading)

- **JavaFX Task / CompletableFuture** – Dùng để xử lý các tác vụ nặng chạy ngầm (như gọi API, tải hình ảnh, và truy vấn cơ sở dữ liệu) mà không làm đơ (freeze) giao diện ứng dụng.

## Cấu trúc thư mục

📁WOWTruyen  
┣📁.idea  
┣📁src/main/java/org/example  
┃ ┣📁api (Giao tiếp HTTP với các API bên ngoài)  
┃ ┣📁app (Điểm khởi chạy chính của ứng dụng)  
┃ ┣📁constant (Các hằng số toàn cục và tin nhắn của hệ thống)  
┃ ┣📁controllers (Các lớp Controller quản lý logic giao diện theo từng chức năng)  
┃ ┣📁dao (Các lớp Data Access Object để tương tác với cơ sở dữ liệu MySQL)  
┃ ┣📁data (Các service dùng để fetch và xử lý dữ liệu API truyện)  
┃ ┣📁exception (Các ngoại lệ tùy chỉnh và bộ xử lý lỗi cho UI)  
┃ ┣📁model (Các lớp Model đại diện cho các bảng trong database và DTO của API)  
┃ ┣📁services (Các interface service và phần triển khai cho các nghiệp vụ cốt lõi)  
┃ ┗📁utils (Các lớp tiện ích hỗ trợ mã hóa, email, load ảnh và điều hướng)  
┣📁src/main/resources  
┃ ┣📁image (Các tài nguyên hình ảnh tĩnh và icon)  
┃ ┗📁view (Các file layout giao diện FXML được sắp xếp theo tính năng)  
┣📜pom.xml (Cấu hình build Maven và các thư viện phụ thuộc)  
┗📜README.md