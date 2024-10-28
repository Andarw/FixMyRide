CREATE TABLE `users` (
  `id` integer PRIMARY KEY,
  `username` varchar(255),
  `password` varchar(255),
  `email` varchar(255)
);

CREATE TABLE `reports` (
  `id` integer PRIMARY KEY,
  `user_id` integer,
  `created_at` timestamp,
  `car_brand` varchar(255),
  `damaged_location` varchar(255) COMMENT 'rear, front, left_door ...',
  `estimated_cost` float,
  `part_links` varchar(255) COMMENT 'will most likely constit of urls to different sites'
);

CREATE TABLE `Images` (
  `id` Integer PRIMARY KEY,
  `image_` image,
  `report_id` integer
);

ALTER TABLE `Images` ADD FOREIGN KEY (`report_id`) REFERENCES `reports` (`id`);

ALTER TABLE `reports` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
