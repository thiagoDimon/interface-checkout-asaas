export function findOptionLabel(options, value) {
  return options.find((option) => option.value === value)?.label ?? value
}
