-- Add memorable_date column to memory table
ALTER TABLE memory ADD COLUMN memorable_date DATE;

-- Add comment for the new column
COMMENT ON COLUMN memory.memorable_date IS '기억해야 할 날짜 (추억의 날짜)';