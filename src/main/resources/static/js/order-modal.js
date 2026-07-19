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
 * Hàm kiểm tra file hình ảnh và hiển thị ảnh xem trước (Preview)
 */
function handleImagePreview(file, previewImgEl, previewContainerEl) {
  if (!file) return;

  // Kiểm tra dung lượng (Max 5MB)
  if (file.size > 5 * 1024 * 1024) {
    alert("File quá lớn! Vui lòng chọn file nhỏ hơn 5MB");
    return;
  }

  // Kiểm tra định dạng file
  if (!file.type.startsWith("image/")) {
    alert("Vui lòng chọn file hình ảnh hợp lệ (jpg, png, gif)!");
    return;
  }

  const reader = new FileReader();
  reader.onload = (e) => {
    if (previewImgEl && previewContainerEl) {
      previewImgEl.src = e.target.result;
      previewContainerEl.classList.remove("hidden");
    }
  };
  reader.readAsDataURL(file);
}

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

/**
 * Hàm thiết lập tính năng Kéo & Thả (Drag & Drop) và Click để chọn ảnh
 */
function setupImageUploadArea(
  modalEl,
  uploadInputEl,
  previewImgEl,
  previewContainerEl,
) {
  if (!uploadInputEl || !modalEl) return;

  const uploadArea = modalEl.querySelector(".upload-zone");
  if (!uploadArea) return;

  // Sự kiện khi click vào vùng dashed border -> Kích hoạt thẻ input file ẩn
  uploadArea.addEventListener("click", () => uploadInputEl.click());

  // Sự kiện khi kéo file đè lên vùng upload
  uploadArea.addEventListener("dragover", (e) => {
    e.preventDefault();
    uploadArea.classList.add("border-purple-500", "bg-purple-50");
  });

  // Sự kiện khi kéo file ra khỏi vùng upload
  uploadArea.addEventListener("dragleave", () => {
    uploadArea.classList.remove("border-purple-500", "bg-purple-50");
  });

  // Sự kiện khi thả file vào vùng upload
  uploadArea.addEventListener("drop", (e) => {
    e.preventDefault();
    uploadArea.classList.remove("border-purple-500", "bg-purple-50");

    const files = e.dataTransfer.files;
    if (files.length > 0) {
      uploadInputEl.files = files; // Gán danh sách file vào thẻ input để khi submit form tự gửi đi
      handleImagePreview(files[0], previewImgEl, previewContainerEl);
    }
  });

  // Lắng nghe sự kiện click nút Xóa ảnh cũ/ảnh preview
  const btnRemoveImg = modalEl.querySelector(".btn-remove-img");
  if (btnRemoveImg) {
    btnRemoveImg.addEventListener("click", (e) => {
      e.preventDefault();
      uploadInputEl.value = "";
      if (previewContainerEl) previewContainerEl.classList.add("hidden");
      if (previewImgEl) previewImgEl.src = "";
    });
  }
}

// =========================================================================
// II. HÀM KHỞI TẠO MODAL TẠO MỚI (CREATE CONTROLLER)
// =========================================================================

function initModalCreateController({
  modalId,
  openBtnId,
  uploadInputId,
  previewContainerId,
  previewImgId,
  submitUrl,
}) {
  const modalEl = document.getElementById(modalId);
  const openBtnEl = document.getElementById(openBtnId);
  const formEl = modalEl?.querySelector("form");
  const uploadInputEl = document.getElementById(uploadInputId);
  const previewContainerEl = document.getElementById(previewContainerId);
  const previewImgEl = document.getElementById(previewImgId);

  // Hàm xử lý đóng đóng modal tạo mới
  const closeModal = () => {
    if (modalEl) {
      modalEl.classList.add("hidden");
      document.body.style.overflow = "auto";
      formEl?.reset(); // Làm sạch form dữ liệu
      if (previewContainerEl) previewContainerEl.classList.add("hidden");
      if (previewImgEl) previewImgEl.src = "";
    }
  };

  // Mở modal khi bấm nút thêm mới ngoài màn hình chính
  if (openBtnEl) {
    openBtnEl.addEventListener("click", async function (e) {
      e.preventDefault();

      // get data accounts
      const response = await fetch("/api/account/list");
      const accountData = await response.json();
      const selectField = document.querySelector('select[name="account"]');
      selectField.innerHTML = '<option value="">-- Chọn Account --</option>';
      accountData.forEach((name) => {
        selectField.innerHTML += `<option value="${name}" >${name}</option>`;
      });

      modalEl?.classList.remove("hidden");
      document.body.style.overflow = "hidden";
    });
  }

  // Khởi tạo các sự kiện đóng/hủy chung
  setupCommonModalEvents(modalEl, closeModal);

  // Khởi tạo sự kiện xử lý ảnh cho modal Create
  if (uploadInputEl) {
    uploadInputEl.addEventListener("change", (e) => {
      handleImagePreview(e.target.files[0], previewImgEl, previewContainerEl);
    });
    setupImageUploadArea(
      modalEl,
      uploadInputEl,
      previewImgEl,
      previewContainerEl,
    );
  }

  // Lắng nghe sự kiện submit form gửi dữ liệu lên server (POST)
  if (formEl) {
    formEl.addEventListener("submit", async function (event) {
      event.preventDefault();
      const formData = new FormData(formEl);

      try {
        const response = await fetch(submitUrl, {
          method: "POST",
          body: formData,
        });

        if (response.ok) {
          alert("Tạo đơn hàng mới thành công!");
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

// =========================================================================
// III. HÀM KHỞI TẠO MODAL CẬP NHẬT (UPDATE CONTROLLER)
// =========================================================================

function initModalUpdateController({
  modalId,
  editBtnClass,
  uploadInputId,
  previewContainerId,
  previewImgId,
  fetchUrlPattern,
  submitUrlPattern,
}) {
  const modalEl = document.getElementById(modalId);
  const formEl = modalEl?.querySelector("form");
  const uploadInputEl = document.getElementById(uploadInputId);
  const previewContainerEl = document.getElementById(previewContainerId);
  const previewImgEl = document.getElementById(previewImgId);

  let currentOrderId = null; // Biến ghi nhớ ID đơn hàng đang mở để sửa

  // Hàm đóng đóng modal cập nhật
  const closeModal = () => {
    if (modalEl) {
      modalEl.classList.add("hidden");
      document.body.style.overflow = "auto";
      formEl?.reset(); // Xóa dữ liệu form
      if (previewContainerEl) previewContainerEl.classList.add("hidden");
      if (previewImgEl) previewImgEl.src = "";
      currentOrderId = null;
    }
  };

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

        if (!response.ok)
          throw new Error("Không lấy được dữ liệu chi tiết đơn");

        const orderData = await response.json();

        // --- TIẾN HÀNH ĐỔ DỮ LIỆU CŨ VÀO FORM (DATA BINDING) ---
        Object.keys(orderData).forEach((key) => {
          const input = formEl.querySelector(`[name="${key}"]`);
          // Chỉ điền dữ liệu vào các input thông thường, bỏ qua các thẻ input chọn file ảnh
          if (input && input.type !== "file") {
            input.value = orderData[key];
          }
        });

        // Xử lý hiển thị ảnh hiện tại đang có trên máy chủ
        if (orderData.productImagePath && previewImgEl && previewContainerEl) {
          let cleanPath = orderData.productImagePath.replace(/\\/g, "/");

          // 2. Đảm bảo có dấu / ở đầu đường dẫn
          if (!cleanPath.startsWith("/")) {
            cleanPath = "/" + cleanPath;
          }
          previewImgEl.src = cleanPath;
          previewContainerEl.classList.remove("hidden");
        }

        // Hiện thị modal lên màn hình sau khi đổ dữ liệu thành công
        modalEl?.classList.remove("hidden");
        document.body.style.overflow = "hidden";
      } catch (error) {
        console.error("Lỗi lấy chi tiết:", error);
        alert("Lỗi tải thông tin chi tiết đơn hàng!");
      }
    });
  });

  // Khởi tạo sự kiện xử lý ảnh cho modal Update
  if (uploadInputEl) {
    uploadInputEl.addEventListener("change", (e) => {
      handleImagePreview(e.target.files[0], previewImgEl, previewContainerEl);
    });
    setupImageUploadArea(
      modalEl,
      uploadInputEl,
      previewImgEl,
      previewContainerEl,
    );
  }

  // Lắng nghe sự kiện submit form gửi dữ liệu cập nhật lên server (PUT)
  if (formEl) {
    formEl.addEventListener("submit", async function (event) {
      event.preventDefault();
      if (!currentOrderId) return;

      const formData = new FormData(formEl);
      const submitUrl = submitUrlPattern.replace("{id}", currentOrderId);

      try {
        const response = await fetch(submitUrl, {
          method: "PUT", // Sử dụng PUT method chuẩn Restful API cho tác vụ cập nhật toàn diện
          body: formData,
        });

        if (response.ok) {
          alert("Cập nhật thông tin đơn hàng thành công!");
          closeModal();
          window.location.reload(); // Làm mới trang để lấy danh sách mới cập nhật
        } else {
          const errorText = await response.text();
          alert("Cập nhật thất bại: " + errorText);
        }
      } catch (error) {
        console.error("Lỗi kết nối:", error);
        alert("Không thể kết nối đến máy chủ khi cập nhật!");
      }
    });
  }
}
