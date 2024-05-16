export function formatDate(date: Date | string): string {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0'); // Months are zero-based, so add 1 and pad with leading zeros if necessary
  const day = String(d.getDate()).padStart(2, '0'); // Pad with leading zeros if necessary
  return `${year}-${month}-${day}`;
}

export function formatTime(date: Date | string): string {
  const d = new Date(date);
  const hours = d.getHours().toString().padStart(2, '0');
  const minutes = d.getMinutes().toString().padStart(2, '0');
  return `${hours}:${minutes}`;
}
