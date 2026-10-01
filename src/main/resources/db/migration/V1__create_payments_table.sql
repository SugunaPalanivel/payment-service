CREATE TABLE payments (
                          id UUID PRIMARY KEY,
                          payment_reference VARCHAR(100) NOT NULL UNIQUE,
                          source_account VARCHAR(100) NOT NULL,
                          destination_account VARCHAR(100) NOT NULL,
                          amount NUMERIC(19,2) NOT NULL,
                          currency VARCHAR(3) NOT NULL,
                          status VARCHAR(30) NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL
);