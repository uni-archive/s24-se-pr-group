export function formatPrice(price: number): string {
  const euro = price / 100;
  return `${euro.toFixed(2)}€`
}
