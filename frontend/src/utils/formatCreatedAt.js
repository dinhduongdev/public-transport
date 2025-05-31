
export const formatCreatedAt = (createdAt) => {
    if (!createdAt || !Array.isArray(createdAt) || createdAt.length < 6) {
      return 'Không xác định';
    }
    const [year, month, day, hour, minute, second] = createdAt;
    const date = new Date(year, month - 1, day, hour, minute, second);
    return date.toLocaleString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    });
  };