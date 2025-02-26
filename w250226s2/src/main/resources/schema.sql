CREATE TABLE IF NOT EXISTS orders (
                                      id SERIAL PRIMARY KEY,
                                      customer_name VARCHAR(255) NOT NULL,
    total_price DECIMAL NOT NULL
    );
