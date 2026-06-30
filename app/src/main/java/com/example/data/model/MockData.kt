package com.example.data.model

object MockData {
    val LAWYERS_LIST = listOf(
        Lawyer(
            id = "L101",
            name = "Adv. Rajesh Kumar Mishra",
            specialty = "Criminal Law / BNS / IPC",
            rating = 4.9f,
            reviewsCount = 142,
            experienceYears = 18,
            startingFee = "₹500",
            city = "Delhi NCR",
            languages = listOf("English", "Hindi"),
            barId = "D/4251/2008",
            responseTime = "Under 10 mins",
            bio = "Rajesh Mishra is a senior advocate at the Delhi High Court specializing in criminal defense, FIR quashing, anticipatory bail, and trials. He is an expert in navigating the transitions from IPC to the new Bharatiya Nyaya Sanhita (BNS).",
            imageRes = 1
        ),
        Lawyer(
            id = "L102",
            name = "Adv. Priya Sharma",
            specialty = "Property & Real Estate Disputes",
            rating = 4.8f,
            reviewsCount = 98,
            experienceYears = 12,
            startingFee = "₹750",
            city = "Mumbai",
            languages = listOf("English", "Hindi", "Marathi"),
            barId = "MAH/1029/2014",
            responseTime = "Under 15 mins",
            bio = "Priya Sharma specializes in property litigation, RERA disputes, landlord-tenant agreements, and property verification checks in Maharashtra and Delhi.",
            imageRes = 2
        ),
        Lawyer(
            id = "L103",
            name = "Adv. Ananth Vardhan",
            specialty = "Consumer Protection & Contracts",
            rating = 4.7f,
            reviewsCount = 64,
            experienceYears = 8,
            startingFee = "₹350",
            city = "Bengaluru",
            languages = listOf("English", "Hindi", "Kannada"),
            barId = "KAR/3295/2018",
            responseTime = "Under 30 mins",
            bio = "Ananth Vardhan helps individuals and startups with consumer court complaints, service deficiency notices, contract drafting, and civil disputes.",
            imageRes = 3
        ),
        Lawyer(
            id = "L104",
            name = "Adv. Meenakshi Iyer",
            specialty = "Family & Matrimonial Law",
            rating = 4.9f,
            reviewsCount = 115,
            experienceYears = 15,
            startingFee = "₹600",
            city = "Chennai",
            languages = listOf("English", "Tamil", "Hindi"),
            barId = "TN/854/2011",
            responseTime = "Under 15 mins",
            bio = "Meenakshi Iyer has represented clients in matrimonial disputes, mutual divorces, domestic violence, child custody, and maintenance trials.",
            imageRes = 4
        ),
        Lawyer(
            id = "L105",
            name = "Adv. Gurbaksh Singh",
            specialty = "Corporate & Contract Review",
            rating = 4.6f,
            reviewsCount = 47,
            experienceYears = 10,
            startingFee = "₹999",
            city = "Chandigarh",
            languages = listOf("English", "Punjabi", "Hindi"),
            barId = "PH/654/2016",
            responseTime = "Under 20 mins",
            bio = "Gurbaksh Singh is a corporate law consultant advising clients on partnership deeds, corporate compliance, MSME recovery suits, and legal contract analysis.",
            imageRes = 5
        )
    )
}
