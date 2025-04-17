import React, { useState } from "react";
import { motion } from "motion/react";

const Card = ({ className = "", children }) => (
    <div className={`bg-white p-6 rounded-2xl shadow ${className}`}>{children}</div>
);

const CardContent = ({ children }) => (
    <div className="space-y-4">{children}</div>
);

const LoginPage = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleLogin = async (e) => {
        e.preventDefault();

        try {
            console.log("login");
            console.log(JSON.stringify({ username, password }));
            const response = await fetch("http://localhost:8080/api/users/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ username, password }),
            });

            if (response.ok) {
                const data = await response.json();
                alert("Login successful! Token: " + data.token);
                setError("");
            } else {
                const err = await response.text();
                setError(err || "Invalid email or password.");
            }
        } catch (err) {
            console.error(err);
            //setError("An error occurred while trying to login.");
        }
    };

    return (
        <motion.div
            className="min-h-screen flex items-center justify-center bg-gray-100"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.5 }}
        >
            <Card className="w-full max-w-sm shadow-lg rounded-2xl">
                <CardContent className="p-6">
                    <h2 className="text-2xl font-bold mb-4 text-center">Login</h2>
                    <form onSubmit={handleLogin} className="space-y-4">
                        <input
                            //type="username"
                            placeholder="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        {error && <p className="text-red-500 text-sm">{error}</p>}
                        <button type="submit" className="w-full">
                            Login
                        </button>
                    </form>
                </CardContent>
            </Card>
        </motion.div>
    );
};

export default LoginPage;
