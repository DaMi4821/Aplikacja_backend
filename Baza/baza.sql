select * from uploaded_files;

CREATE TABLE uploaded_files (
    id SERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_price VARCHAR(255) NOT NULL,
    category_id INT NOT NULL
);

INSERT INTO uploaded_files (file_name, file_path, file_price, category_id) VALUES ('1.jpg', 'Obrazy/suplementy/1.jpg', '116.49 zł', 1);
INSERT INTO uploaded_files (file_name, file_path, file_price, category_id) VALUES ('2.jpg', 'Obrazy/suplementy/2.jpg', '26.49 zł', 1);
INSERT INTO uploaded_files (file_name, file_path, file_price, category_id) VALUES ('3.jpg', 'Obrazy/suplementy/3.jpg', '96.49 zł', 1);
INSERT INTO uploaded_files (file_name, file_path, file_price, category_id) VALUES ('4.jpg', 'Obrazy/suplementy/4.jpg', '73.49 zł', 1);
INSERT INTO uploaded_files (file_name, file_path, file_price, category_id) VALUES ('5.jpg', 'Obrazy/suplementy/5.jpg', '116.49 zł', 1);
INSERT INTO uploaded_files (file_name, file_path, file_price, category_id) VALUES ('6.jpg', 'Obrazy/suplementy/6.jpg', '26.49 zł', 1);
INSERT INTO uploaded_files (file_name, file_path, file_price, category_id) VALUES ('7.jpg', 'Obrazy/suplementy/7.jpg', '96.49 zł', 1);
INSERT INTO uploaded_files (file_name, file_path, file_price, category_id) VALUES ('8.jpg', 'Obrazy/suplementy/8.jpg', '73.49 zł', 1);
INSERT INTO uploaded_files (file_name, file_path, file_price, category_id) VALUES ('9.jpg', 'Obrazy/suplementy/7.jpg', '23.49 zł', 1);
INSERT INTO uploaded_files (file_name, file_path, file_price, category_id) VALUES ('10.jpg', 'Obrazy/suplementy/8.jpg', '85.49 zł', 1);

DROP TABLE IF EXISTS uploaded_files;