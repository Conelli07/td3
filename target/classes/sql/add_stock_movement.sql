CREATE TYPE movement_type AS ENUM ('IN', 'OUT');

CREATE TABLE StockMovement (
    id SERIAL PRIMARY KEY,
    ingredient_id INTEGER NOT NULL,
    quantity DECIMAL(10, 3) NOT NULL,
    unit unit_type NOT NULL,
    movement_type movement_type NOT NULL,
    creation_datetime TIMESTAMP NOT NULL,
    FOREIGN KEY (ingredient_id) REFERENCES Ingredient(id) ON DELETE CASCADE
);

CREATE INDEX idx_stock_movement_ingredient ON StockMovement(ingredient_id);
CREATE INDEX idx_stock_movement_date ON StockMovement(creation_datetime);