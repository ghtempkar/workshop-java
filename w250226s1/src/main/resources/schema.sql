CREATE TABLE orders (
                        id IDENTITY PRIMARY KEY,
                        customer_name VARCHAR(255) NOT NULL,
                        total_price DECIMAL NOT NULL
);
