const express = require("express");
const router = express.Router();
const controller = require("../controllers/warehouseItemsController");

router.get("/", controller.getWarehouseItems);

module.exports = router;
