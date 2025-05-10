DROP TABLE IF EXISTS batteries;
CREATE TABLE IF NOT EXISTS batteries (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    postcode VARCHAR(10) NOT NULL,
    watt_capacity DOUBLE PRECISION NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_batteries_postcode ON batteries(postcode);
CREATE INDEX IF NOT EXISTS idx_batteries_watt_capacity ON batteries(watt_capacity);
