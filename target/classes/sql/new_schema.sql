DROP TABLE IF EXISTS DishIngredient CASCADE;
DROP TABLE IF EXISTS Ingredient CASCADE;
DROP TABLE IF EXISTS Dish CASCADE;
DROP TYPE IF EXISTS dish_type CASCADE;
DROP TYPE IF EXISTS ingredient_category CASCADE;
DROP TYPE IF EXISTS unit_type CASCADE;

CREATE TYPE dish_type AS ENUM ('START', 'MAIN', 'DESSERT');
CREATE TYPE ingredient_category AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
CREATE TYPE unit_type AS ENUM ('PCS', 'KG', 'L');

CREATE TABLE Dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dish_type dish_type NOT NULL,
    price DECIMAL(10, 2) NULL
);

CREATE TABLE Ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    price DECIMAL(10, 2) NOT NULL,
    category ingredient_category NOT NULL
);

CREATE TABLE DishIngredient (
    id SERIAL PRIMARY KEY,
    dish_id INTEGER NOT NULL,
    ingredient_id INTEGER NOT NULL,
    quantity DECIMAL(10, 3) NOT NULL,
    unit unit_type NOT NULL,
    FOREIGN KEY (dish_id) REFERENCES Dish(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES Ingredient(id) ON DELETE CASCADE,
    UNIQUE(dish_id, ingredient_id)
);

INSERT INTO Dish (id, name, dish_type, price) VALUES
    (1, 'Salade fraiche', 'START', 3500.00),
    (2, 'Poulet grille', 'MAIN', 12000.00),
    (3, 'Riz aux legumes', 'MAIN', NULL),
    (4, 'Gateau au chocolat', 'DESSERT', 8000.00),
    (5, 'Salade de fruits', 'DESSERT', NULL);

INSERT INTO Ingredient (id, name, price, category) VALUES
    (1, 'Laitue', 800.00, 'VEGETABLE'),
    (2, 'Tomate', 600.00, 'VEGETABLE'),
    (3, 'Poulet', 4500.00, 'ANIMAL'),
    (4, 'Chocolat', 3000.00, 'OTHER'),
    (5, 'Beurre', 2500.00, 'DAIRY');

INSERT INTO DishIngredient (id, dish_id, ingredient_id, quantity, unit) VALUES
    (1, 1, 1, 0.20, 'KG'),
    (2, 1, 2, 0.15, 'KG'),
    (3, 2, 3, 1.00, 'KG'),
    (4, 4, 4, 0.30, 'KG'),
    (5, 4, 5, 0.20, 'KG');

SELECT setval('dish_id_seq', (SELECT MAX(id) FROM Dish));
SELECT setval('ingredient_id_seq', (SELECT MAX(id) FROM Ingredient));
SELECT setval('dishingredient_id_seq', (SELECT MAX(id) FROM DishIngredient));