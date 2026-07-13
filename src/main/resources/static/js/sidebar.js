const userButton = document.getElementById("userButton");
const dropdownMenu = document.getElementById("dropdownMenu");
const backdrop = document.getElementById("backdrop");
const chevronIcon = document.getElementById("chevronIcon");

function toggleDropdown() {
  const isOpen = dropdownMenu.classList.contains("hidden");
  console.log("open");

  if (isOpen) {
    dropdownMenu.classList.remove("hidden");
    backdrop.classList.remove("hidden");
    chevronIcon.style.transform = "rotate(180deg)";
    userButton.setAttribute("aria-expanded", "true");
  } else {
    dropdownMenu.classList.add("hidden");
    backdrop.classList.add("hidden");
    chevronIcon.style.transform = "rotate(0deg)";
    userButton.setAttribute("aria-expanded", "false");
  }
}

function closeDropdown() {
  dropdownMenu.classList.add("hidden");
  backdrop.classList.add("hidden");
  chevronIcon.style.transform = "rotate(0deg)";
  userButton.setAttribute("aria-expanded", "false");
}

// User button click handler
userButton.addEventListener("click", toggleDropdown);

// Close dropdown when clicking outside
backdrop.addEventListener("click", closeDropdown);

// Keyboard accessibility
document.addEventListener("keydown", (e) => {
  if (e.key === "Escape") {
    closeDropdown();
  }
});
