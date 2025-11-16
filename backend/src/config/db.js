const mongoose = require('mongoose');
require('dotenv').config();

async function connectMongoDB() {
    try {
        await mongoose.connect(process.env.MONGO_URI);
        console.log("Connected to MongoDB...");
    } catch (err) {
        console.error("MongoDB connection error:", err.message);
    }
}

module.exports = connectMongoDB;
