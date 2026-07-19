/**
 * =========================================================================
 * ORDER MODAL MANAGEMENT SYSTEM
 * =========================================================================
 * File này quản lý toàn bộ logic tương tác, xử lý dữ liệu và kết nối API
 * cho cả hai hành động THÊM MỚI (Create) và CẬP NHẬT (Update) đơn hàng.
 */

// =========================================================================
// I. CÁC HÀM TIỆN ÍCH DÙNG CHUNG (PRIVATE HELPERS)
// =========================================================================

/**
 * Hàm thiết lập các sự kiện đóng modal cơ bản (Click nền đen, Click nút hủy)
 */
function setupCommonModalEvents(modalEl, closeModalFn) {
  if (!modalEl) return;

  // 1. Đóng modal khi người dùng click chính xác vào vùng nền đen phía ngoài
  modalEl.addEventListener("click", function (event) {
    if (event.target === this) {
      closeModalFn();
    }
  });

  // 2. Tìm và gán sự kiện cho toàn bộ nút có class '.btn-close-modal' nằm trong modal này
  modalEl.querySelectorAll(".btn-close-modal").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      closeModalFn();
    });
  });
}
// =========================================================================
// II. HÀM KHỞI TẠO MODAL TẠO MỚI (CREATE CONTROLLER)
// =========================================================================

function initModalUpdateWarehouseController({
  modalId,
  resetBtnId,
  editBtnClass,
  fetchUrlPattern,
  submitUrlPattern,
}) {
  const modalEl = document.getElementById(modalId);
  const resetBtnEl = document.getElementById(resetBtnId);
  const formEl = modalEl?.querySelector("form");

  // Hàm xử lý đóng đóng modal tạo mới
  const closeModal = () => {
    if (modalEl) {
      modalEl.classList.add("hidden");
      document.body.style.overflow = "auto";
      formEl?.reset(); // Làm sạch form dữ liệu
    }
  };

  // Reset data modal khi bấm nút reset
  if (resetBtnEl) {
    resetBtnEl.addEventListener("click", (e) => {
      e.preventDefault();

      const trackingInput = formEl.querySelector('[name="tracking"]');
      const orderNumberInput = formEl.querySelector(
        '[name="orderNumberWarehouse"]',
      );
      const zipInput = formEl.querySelector('[name="zip"]');
      const emailnput = formEl.querySelector('[name="email"]');
      const phoneNumberInput = formEl.querySelector('[name="phoneNumber"]');
      const password = formEl.querySelector('[name="password"]');
      const linkEvidence = formEl.querySelector('[name="linkEvidence"]');

      // Lay du lieu cu
      let text = "";
      if (trackingInput != "")
        text += "Tracking: " + trackingInput.value.trim() + "\n";
      if (orderNumberInput != "")
        text += "Order Number: " + orderNumberInput.value.trim() + "\n";
      if (zipInput != "") text += "Zip: " + zipInput.value.trim() + "\n";
      if (emailnput != "") text += "Email: " + emailnput.value.trim() + "\n";
      if (phoneNumberInput != "")
        text += "Phone Number: " + phoneNumberInput.value.trim() + "\n";
      if (password != "") text += "Password: " + password.value.trim() + "\n";
      if (linkEvidence != "")
        text += "Evidence: " + linkEvidence.value.trim() + "\n";
      text += "***" + "\n\n";

      // truyen vao field noteWarehouse
      const noteWarehouseInput = formEl.querySelector('[name="noteWarehouse"]');
      noteWarehouseInput.value = text;
      trackingInput.value = "";
      orderNumberInput.value = "";
      zipInput.value = "";
      emailnput.value = "";
      phoneNumberInput.value = "";
      password.value = "";
      linkEvidence.value = "";
    });
  }

  // Khởi tạo các sự kiện đóng/hủy chung
  setupCommonModalEvents(modalEl, closeModal);

  // Lắng nghe hành động khi người dùng nhấn nút Sửa trên bảng danh sách
  document.querySelectorAll(`.${editBtnClass}`).forEach((btn) => {
    btn.addEventListener("click", async function (e) {
      e.preventDefault();

      // Lấy ID đơn hàng được lưu tại thuộc tính data-id của dòng/nút bấm đó
      currentOrderId = this.getAttribute("data-id");
      if (!currentOrderId) return;

      try {
        // Thay thế chuỗi đại diện {id} bằng mã đơn thực tế
        const fetchUrl = fetchUrlPattern.replace("{id}", currentOrderId);
        const response = await fetch(fetchUrl);

        if (response.ok) {
          if (response.status == 200) {
            const orderData = await response.json();

            // --- TIẾN HÀNH ĐỔ DỮ LIỆU CŨ VÀO FORM (DATA BINDING) ---
            Object.keys(orderData).forEach((key) => {
              const input = formEl.querySelector(`[name="${key}"]`);
              // Chỉ điền dữ liệu vào các input thông thường, bỏ qua các thẻ input chọn file ảnh
              if (input) {
                input.value = orderData[key];
              }
            });
          }

          // Hiện thị modal lên màn hình sau khi đổ dữ liệu thành công
          modalEl?.classList.remove("hidden");
          document.body.style.overflow = "hidden";
        }
      } catch (error) {
        console.error("Lỗi lấy chi tiết:", error);
        alert("Lỗi tải thông tin chi tiết đơn hàng!");
      }
    });
  });

  // Lắng nghe sự kiện submit form gửi dữ liệu lên server (POST)
  if (formEl) {
    formEl.addEventListener("submit", async function (event) {
      event.preventDefault();
      const formData = new FormData(formEl);

      try {
        const fetchUrl = submitUrlPattern.replace("{id}", currentOrderId);
        const response = await fetch(fetchUrl, {
          method: "POST",
          body: formData,
        });

        if (response.ok) {
          closeModal();
          window.location.reload(); // Reload lại trang để cập nhật bảng
        } else {
          const errorText = await response.text();
          alert("Có lỗi xảy ra từ máy chủ: " + errorText);
        }
      } catch (error) {
        console.error("Lỗi kết nối:", error);
        alert("Không thể kết nối đến máy chủ!");
      }
    });
  }
}
