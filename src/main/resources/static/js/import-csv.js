const btnImport = document.getElementById("btn-import-csv");
const fileInput = document.getElementById("csv-file-input");

// Bước 1: Khi click button đẹp, kích hoạt mở popup chọn file của input ẩn
btnImport.addEventListener("click", () => {
  fileInput.click();
});

// Bước 2: Khi người dùng đã chọn file xong (Sự kiện change)
fileInput.addEventListener("change", function () {
  const file = this.files[0];
  if (!file) return;

  // Kiểm tra nhanh định dạng file ở client
  if (!file.name.endsWith(".csv")) {
    alert("Vui lòng chọn đúng file định dạng .csv");
    this.value = ""; // Reset input
    return;
  }

  // Tạo FormData để chuẩn bị gửi qua API
  const formData = new FormData();
  formData.append("file", file);

  // Đổi trạng thái button để người dùng biết hệ thống đang xử lý
  btnImport.disabled = true;

  // Bước 3: Gửi file lên backend bằng Fetch API
  fetch("/order/import-csv", {
    method: "POST",
    body: formData,
  })
    .then((response) => {
      if (response.ok) return response.text();
      throw new Error("Có lỗi xảy ra khi xử lý file ở hệ thống.");
    })
    .then((data) => {
      alert("Import dữ liệu thành công!");
      location.reload(); // Reload lại trang để cập nhật bảng dữ liệu mới
    })
    .catch((error) => {
      alert(error.message);
    })
    .finally(() => {
      // Trả lại trạng thái ban đầu cho button
      btnImport.disabled = false;
      fileInput.value = ""; // Reset input để có thể chọn lại file đó nếu cần
    });
});
