CREATE TABLE bank_accounts (
  id serial PRIMARY KEY,
  user_id integer REFERENCES users (id) ON delete cascade NOT NULL,
  name varchar(255) NOT NULL,
  bank_name varchar(255),
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX bank_accounts_user ON bank_accounts (id, user_id);
CREATE UNIQUE INDEX unique_name ON bank_accounts ((LOWER(name)), user_id);
