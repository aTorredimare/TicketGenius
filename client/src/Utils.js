import { toast } from "react-toastify"

// function to send to toast the message to display
export const notify = (message, bool = true) => setTimeout(() => {bool ? toast.success(message) : toast.error(message)}, 200)