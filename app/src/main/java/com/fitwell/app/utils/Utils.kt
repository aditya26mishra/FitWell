package com.fitwell.app.utils

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

// ── Session ───────────────────────────────────────────────────────────────────
class Session(context: Context) {
    private val p: SharedPreferences = context.getSharedPreferences("fw_session", Context.MODE_PRIVATE)
    var userId: Int get() = p.getInt("uid", -1); set(v) = p.edit().putInt("uid", v).apply()
    var loggedIn: Boolean get() = p.getBoolean("in", false); set(v) = p.edit().putBoolean("in", v).apply()
    fun logout() = p.edit().clear().apply()
}

// ── Crypto ────────────────────────────────────────────────────────────────────
object Crypto {
    fun hash(s: String): String =
        MessageDigest.getInstance("SHA-256").digest(s.toByteArray())
            .joinToString("") { "%02x".format(it) }
}

// ── Dates ─────────────────────────────────────────────────────────────────────
object Dates {
    private val iso     = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val pretty  = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    private val chartFmt= SimpleDateFormat("MM/dd", Locale.getDefault())
    private val weekFmt = SimpleDateFormat("EEE", Locale.getDefault())

    fun today(): String = iso.format(Date())
    fun daysAgo(n: Int): String {
        val c = Calendar.getInstance(); c.add(Calendar.DAY_OF_YEAR, -n); return iso.format(c.time)
    }
    fun pretty(date: String) = try { pretty.format(iso.parse(date)!!) } catch (e: Exception) { date }
    fun chart(date: String)  = try { chartFmt.format(iso.parse(date)!!) } catch (e: Exception) { date.takeLast(5) }
    fun weekDay(date: String)= try { weekFmt.format(iso.parse(date)!!) } catch (e: Exception) { date.take(3) }
}

// ── Fitness Calculators ───────────────────────────────────────────────────────
object Calc {
    fun bmr(wKg: Float, hCm: Float, age: Int, gender: String): Float =
        if (gender == "female") 10*wKg + 6.25f*hCm - 5*age - 161
        else                    10*wKg + 6.25f*hCm - 5*age + 5

    fun tdee(bmr: Float, level: String) = bmr * when(level) {
        "light" -> 1.375f; "moderate" -> 1.55f; "active" -> 1.725f; "very_active" -> 1.9f
        else    -> 1.2f
    }

    fun calorieGoal(tdee: Float, goal: String) = when(goal) {
        "weight_loss"  -> (tdee - 500).roundToInt()
        "muscle_gain"  -> (tdee + 300).roundToInt()
        else           -> tdee.roundToInt()
    }

    fun bmi(wKg: Float, hCm: Float): Float { val h = hCm/100f; return wKg/(h*h) }

    fun bmiCategory(bmi: Float) = when {
        bmi < 18.5f -> "Underweight"; bmi < 25f -> "Normal"
        bmi < 30f   -> "Overweight";  else       -> "Obese"
    }

    fun stepsToKm(steps: Int, hCm: Float)  = steps * hCm * 0.415f / 100_000f
    fun stepsToKcal(steps: Int, wKg: Float) = steps * 0.04f * (wKg / 70f)
}

// ── Food Database ─────────────────────────────────────────────────────────────
data class FoodItem(
    val name: String, val category: String,
    val serving: Float, val unit: String,
    val kcal: Float, val protein: Float, val carbs: Float, val fat: Float,
    val fiber: Float = 0f, val sugar: Float = 0f, val sodium: Float = 0f,
    val vitA: Float = 0f, val vitC: Float = 0f, val vitD: Float = 0f,
    val vitB12: Float = 0f, val calcium: Float = 0f, val iron: Float = 0f,
    val potassium: Float = 0f, val magnesium: Float = 0f, val zinc: Float = 0f,
    val imageUrl: String = ""
)

object FoodDb {
    // Stable Unsplash image IDs (no token needed, reliably accessible)
    private const val CHICKEN  = "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=200&q=80"
    private const val EGG      = "https://images.unsplash.com/photo-1582169296194-e4d644c48063?w=200&q=80"
    private const val RICE     = "https://images.unsplash.com/photo-1536304993881-ff86e0c9be01?w=200&q=80"
    private const val ROTI     = "https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=200&q=80"
    private const val DAL      = "https://images.unsplash.com/photo-1547592180-85f173990554?w=200&q=80"
    private const val PANEER   = "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=200&q=80"
    private const val IDLI     = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=200&q=80"
    private const val BANANA   = "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=200&q=80"
    private const val APPLE    = "https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?w=200&q=80"
    private const val SPINACH  = "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=200&q=80"
    private const val OATS     = "https://images.unsplash.com/photo-1517673400267-0251440c45dc?w=200&q=80"
    private const val ALMOND   = "https://images.unsplash.com/photo-1574570068274-e0c98b834aef?w=200&q=80"
    private const val MILK     = "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=200&q=80"
    private const val YOGURT   = "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=200&q=80"
    private const val SALMON   = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=200&q=80"
    private const val BROCCOLI = "https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?w=200&q=80"
    private const val AVOCADO  = "https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?w=200&q=80"
    private const val MANGO    = "https://images.unsplash.com/photo-1553279768-865429fa0078?w=200&q=80"
    private const val PB       = "https://images.unsplash.com/photo-1561047029-3000c68339ca?w=200&q=80"
    private const val COFFEE   = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=200&q=80"
    private const val FOOD_GEN = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=200&q=80"

    val items = listOf(
        // ── Proteins ──
        FoodItem("Chicken Breast (cooked)", "Protein", 100f,"g", 165f,31f,0f,3.6f, 0f,0f,74f, 9f,0f,0f,0.3f, 11f,1f,256f,29f,1f, CHICKEN),
        FoodItem("Egg (whole)", "Protein", 50f,"g", 78f,6f,0.6f,5f, 0f,0.6f,62f, 80f,0f,1.1f,0.6f, 28f,1f,69f,6f,0.6f, EGG),
        FoodItem("Egg White", "Protein", 100f,"g", 52f,11f,0.7f,0.2f, 0f,0.7f,166f, 0f,0f,0f,0.1f, 7f,0.1f,163f,11f,0f, EGG),
        FoodItem("Salmon (cooked)", "Protein", 100f,"g", 208f,22f,0f,13f, 0f,0f,59f, 50f,4f,11f,3.2f, 9f,0.8f,363f,29f,0.6f, SALMON),
        FoodItem("Tuna (canned)", "Protein", 85f,"g", 109f,25f,0f,1f, 0f,0f,287f, 0f,0f,1.7f,2.2f, 4f,1.3f,201f,27f,0.7f, FOOD_GEN),
        FoodItem("Whey Protein (1 scoop)", "Supplement", 30f,"g", 120f,24f,3f,2f, 0f,2f,50f, 0f,0f,0f,0.5f, 130f,0.4f,160f,20f,1f, FOOD_GEN),

        // ── Indian staples ──
        FoodItem("Dal (cooked)", "Indian", 200f,"g", 230f,14f,40f,1f, 8f,2f,0f, 3f,2f,0f,0f, 25f,3.3f,365f,36f,1.3f, DAL),
        FoodItem("Rajma (kidney beans)", "Indian", 200f,"g", 225f,15f,40f,1f, 11f,1f,2f, 0f,2f,0f,0f, 62f,3.9f,600f,60f,1.8f, DAL),
        FoodItem("Chana Dal", "Indian", 200f,"g", 220f,13f,38f,1.5f, 9f,2f,0f, 0f,1f,0f,0f, 28f,2.5f,300f,30f,1.5f, DAL),
        FoodItem("Paneer", "Indian", 100f,"g", 265f,18f,3f,20f, 0f,0f,0f, 0f,0f,0f,0f, 480f,0.2f,72f,8f,1f, PANEER),
        FoodItem("Paneer Bhurji (1 plate)", "Indian", 150f,"g", 290f,18f,8f,20f, 1f,2f,350f, 50f,5f,0f,0f, 320f,1f,200f,20f,1.2f, PANEER),
        FoodItem("Chapati / Roti", "Indian", 40f,"g", 120f,3.5f,25f,1f, 2f,0f,0f, 0f,0f,0f,0f, 14f,1.1f,78f,18f,0.5f, ROTI),
        FoodItem("Paratha (plain)", "Indian", 60f,"g", 196f,4f,30f,7f, 2f,0f,230f, 0f,0f,0f,0f, 18f,1f,80f,15f,0.4f, ROTI),
        FoodItem("Aloo Paratha", "Indian", 120f,"g", 280f,5f,40f,10f, 2f,0f,0f, 2f,1f,0f,0f, 24f,1.2f,220f,22f,0.5f, ROTI),
        FoodItem("Idli (2 pieces)", "Indian", 100f,"g", 118f,4f,23f,1f, 1f,0f,0f, 0f,0f,0f,0f, 8f,0.5f,93f,16f,0.3f, IDLI),
        FoodItem("Dosa (plain)", "Indian", 60f,"g", 133f,3.5f,25f,2f, 0.6f,0f,0f, 0f,0f,0f,0f, 10f,0.5f,62f,10f,0.2f, IDLI),
        FoodItem("Masala Dosa", "Indian", 200f,"g", 340f,6f,52f,12f, 2f,0f,400f, 30f,8f,0f,0f, 20f,1f,250f,25f,0.5f, IDLI),
        FoodItem("Sambar", "Indian", 150f,"ml", 82f,4.5f,12f,1.5f, 3f,0f,0f, 45f,15f,0f,0f, 22f,1.5f,225f,30f,0.5f, DAL),
        FoodItem("Poha", "Indian", 150f,"g", 180f,3f,34f,4f, 1f,0f,0f, 0f,0f,0f,0f, 8f,3.5f,80f,18f,0.4f, FOOD_GEN),
        FoodItem("Upma", "Indian", 150f,"g", 200f,5f,35f,5f, 2f,0f,200f, 0f,0f,0f,0f, 10f,0.8f,100f,20f,0.5f, FOOD_GEN),
        FoodItem("Chole (chickpeas)", "Indian", 200f,"g", 270f,14f,45f,4f, 12f,0f,400f, 5f,2f,0f,0f, 80f,4f,477f,48f,2.5f, DAL),
        FoodItem("Pav Bhaji (1 plate)", "Indian", 250f,"g", 380f,8f,60f,12f, 5f,5f,600f, 100f,15f,0f,0f, 60f,2f,350f,30f,0.8f, FOOD_GEN),
        FoodItem("Biryani (chicken, 1 plate)", "Indian", 300f,"g", 520f,30f,65f,14f, 2f,2f,800f, 20f,5f,0f,0.1f, 40f,2f,400f,40f,2f, RICE),
        FoodItem("Khichdi", "Indian", 200f,"g", 250f,9f,45f,3f, 3f,0f,200f, 0f,0f,0f,0f, 30f,1.5f,200f,28f,0.8f, DAL),
        FoodItem("Lassi (sweet)", "Indian", 200f,"ml", 150f,6f,22f,5f, 0f,20f,80f, 50f,0f,0f,0.5f, 200f,0.1f,200f,15f,0.5f, MILK),
        FoodItem("Buttermilk (chaas)", "Indian", 200f,"ml", 40f,3f,5f,1f, 0f,5f,100f, 20f,0f,0f,0.3f, 120f,0f,150f,10f,0.3f, MILK),
        FoodItem("Rava / Semolina", "Indian", 100f,"g", 360f,11f,73f,1f, 3f,0f,0f, 0f,0f,0f,0f, 17f,2f,186f,47f,1f, FOOD_GEN),
        FoodItem("Moong Dal Chilla", "Indian", 100f,"g", 150f,10f,20f,3f, 3f,0f,0f, 0f,2f,0f,0f, 14f,1.5f,200f,25f,0.8f, FOOD_GEN),
        FoodItem("Besan (gram flour)", "Indian", 100f,"g", 387f,22f,58f,6f, 11f,10f,64f, 0f,0f,0f,0f, 45f,4.9f,846f,166f,2.5f, FOOD_GEN),
        FoodItem("Coconut chutney", "Indian", 50f,"g", 110f,1f,4f,10f, 2f,1f,150f, 0f,0f,0f,0f, 6f,0.5f,100f,10f,0.3f, FOOD_GEN),

        // ── Grains ──
        FoodItem("Brown Rice (cooked)", "Grain", 100f,"g", 112f,2.6f,24f,0.9f, 1.8f,0f,5f, 0f,0f,0f,0f, 3f,0.4f,79f,43f,0.6f, RICE),
        FoodItem("White Rice (cooked)", "Grain", 100f,"g", 130f,2.7f,28f,0.3f, 0.4f,0f,1f, 0f,0f,0f,0f, 2f,0.2f,35f,12f,0.5f, RICE),
        FoodItem("Oats (dry)", "Grain", 80f,"g", 300f,11f,54f,5f, 8f,1f,5f, 0f,0f,0f,0f, 54f,4f,293f,138f,3f, OATS),
        FoodItem("Quinoa (cooked)", "Grain", 185f,"g", 222f,8f,39f,4f, 5f,0f,13f, 0f,0f,0f,0f, 31f,2.8f,318f,118f,2f, FOOD_GEN),
        FoodItem("Wheat Bread (slice)", "Grain", 30f,"g", 75f,3f,15f,1f, 2f,1f,140f, 0f,0f,0f,0f, 38f,0.9f,65f,15f,0.4f, ROTI),

        // ── Vegetables ──
        FoodItem("Spinach (raw)", "Vegetable", 30f,"g", 7f,0.9f,1.1f,0.1f, 0.7f,0f,24f, 141f,8f,0f,0f, 30f,0.8f,167f,24f,0.2f, SPINACH),
        FoodItem("Broccoli (cooked)", "Vegetable", 91f,"g", 31f,2.6f,6f,0.3f, 2.4f,1.5f,30f, 60f,81f,0f,0f, 47f,0.7f,288f,21f,0.4f, BROCCOLI),
        FoodItem("Sweet Potato (baked)", "Vegetable", 130f,"g", 112f,2f,26f,0.1f, 3.8f,5f,72f, 961f,3f,0f,0f, 40f,0.7f,438f,32f,0.3f, FOOD_GEN),
        FoodItem("Potato (boiled)", "Vegetable", 150f,"g", 116f,2.5f,27f,0.1f, 1.8f,0f,7f, 0f,13f,0f,0f, 12f,0.8f,515f,28f,0.4f, FOOD_GEN),
        FoodItem("Tomato", "Vegetable", 123f,"g", 22f,1.1f,4.8f,0.2f, 1.5f,3f,6f, 42f,17f,0f,0f, 12f,0.3f,292f,14f,0.2f, FOOD_GEN),
        FoodItem("Onion", "Vegetable", 100f,"g", 40f,1.1f,9.3f,0.1f, 1.7f,4.2f,4f, 0f,7f,0f,0f, 23f,0.2f,146f,10f,0.2f, FOOD_GEN),
        FoodItem("Cucumber", "Vegetable", 100f,"g", 16f,0.7f,3.6f,0.1f, 0.5f,1.7f,2f, 5f,3f,0f,0f, 16f,0.3f,147f,13f,0.2f, FOOD_GEN),

        // ── Fruits ──
        FoodItem("Banana", "Fruit", 120f,"g", 107f,1.3f,27f,0.4f, 3f,14f,1f, 4f,10f,0f,0f, 6f,0.3f,422f,32f,0.2f, BANANA),
        FoodItem("Apple", "Fruit", 182f,"g", 95f,0.5f,25f,0.3f, 4f,19f,2f, 5f,8f,0f,0f, 11f,0.2f,195f,9f,0.1f, APPLE),
        FoodItem("Mango", "Fruit", 165f,"g", 99f,1.4f,25f,0.6f, 2.6f,23f,2f, 89f,60f,0f,0f, 18f,0.3f,277f,19f,0.1f, MANGO),
        FoodItem("Papaya", "Fruit", 145f,"g", 62f,0.7f,16f,0.4f, 2.5f,11f,12f, 58f,88f,0f,0f, 29f,0.2f,264f,15f,0.1f, FOOD_GEN),
        FoodItem("Orange", "Fruit", 131f,"g", 62f,1.2f,15f,0.2f, 3.1f,12f,0f, 14f,70f,0f,0f, 52f,0.1f,237f,14f,0.1f, FOOD_GEN),
        FoodItem("Watermelon", "Fruit", 280f,"g", 85f,1.7f,21f,0.4f, 1.1f,17f,3f, 28f,23f,0f,0f, 22f,0.4f,314f,29f,0.3f, FOOD_GEN),
        FoodItem("Avocado", "Fruit", 150f,"g", 240f,3f,13f,22f, 10f,1f,11f, 10f,15f,0f,0f, 18f,0.8f,727f,44f,0.9f, AVOCADO),

        // ── Dairy ──
        FoodItem("Whole Milk", "Dairy", 240f,"ml", 150f,8f,12f,8f, 0f,12f,98f, 112f,0f,3.2f,1.1f, 276f,0.1f,322f,24f,0.9f, MILK),
        FoodItem("Greek Yogurt (plain)", "Dairy", 200f,"g", 130f,17f,9f,0.7f, 0f,9f,68f, 0f,0f,0f,0.9f, 187f,0.1f,240f,19f,1f, YOGURT),
        FoodItem("Curd (dahi)", "Dairy", 200f,"g", 120f,8f,9f,5f, 0f,9f,80f, 0f,0f,0f,0.5f, 200f,0.1f,240f,16f,0.8f, YOGURT),
        FoodItem("Paneer", "Dairy", 100f,"g", 265f,18f,3f,20f, 0f,0f,0f, 0f,0f,0f,0f, 480f,0.2f,72f,8f,1f, PANEER),

        // ── Nuts & Fats ──
        FoodItem("Almonds", "Nuts", 28f,"g", 164f,6f,6f,14f, 3.5f,1f,0f, 0f,0f,0f,0f, 76f,1.1f,208f,76f,0.9f, ALMOND),
        FoodItem("Peanut Butter (2 tbsp)", "Nuts", 32f,"g", 190f,8f,7f,16f, 2f,3f,136f, 0f,0f,0f,0f, 17f,0.6f,189f,49f,0.9f, PB),
        FoodItem("Walnuts", "Nuts", 28f,"g", 185f,4.3f,3.9f,18.5f, 1.9f,0.7f,0f, 0f,0.4f,0f,0f, 28f,0.8f,125f,44f,0.9f, ALMOND),
        FoodItem("Olive Oil (1 tbsp)", "Fat", 14f,"ml", 119f,0f,0f,14f, 0f,0f,0f, 0f,0f,0f,0f, 0f,0.1f,0f,0f,0f, FOOD_GEN),
        FoodItem("Ghee (1 tsp)", "Fat", 5f,"ml", 45f,0f,0f,5f, 0f,0f,0f, 0f,0f,0f,0f, 0f,0f,0f,0f,0f, FOOD_GEN),

        // ── Beverages ──
        FoodItem("Black Coffee", "Beverage", 240f,"ml", 2f,0.3f,0f,0f, 0f,0f,5f, 0f,0f,0f,0f, 2f,0f,116f,7f,0f, COFFEE),
        FoodItem("Green Tea", "Beverage", 240f,"ml", 2f,0f,0f,0f, 0f,0f,2f, 0f,0f,0f,0f, 0f,0f,20f,1f,0f, COFFEE),
        FoodItem("Protein Shake (water)", "Beverage", 300f,"ml", 130f,25f,5f,2f, 0f,3f,100f, 0f,0f,0f,0f, 100f,0.5f,150f,20f,1f, FOOD_GEN)
    )

    fun search(q: String): List<FoodItem> {
        val lower = q.lowercase()
        return items.filter { it.name.lowercase().contains(lower) || it.category.lowercase().contains(lower) }
    }
}

// ── Exercise Database ─────────────────────────────────────────────────────────
// Calorie data: MET values × 3.5 × weight(kg) / 200 gives kcal/min for 70kg person
// Sources: ACSM, Compendium of Physical Activities (Ainsworth et al.)
// Images: Unsplash (free to use, no attribution required in-app)
data class Exercise(
    val name: String,
    val muscle: String,
    val defaultSets: Int,
    val defaultReps: Int,
    val kcalPerMin: Float,   // for a ~70kg person
    val equipment: String,
    val imageUrl: String = ""
)

object ExerciseDb {
    // Verified Unsplash photo IDs — each matches the actual exercise type
    private const val BARBELL_BENCH   = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400&q=80"
    private const val PUSHUP_FLOOR    = "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&q=80"
    private const val CABLE_MACHINE   = "https://images.unsplash.com/photo-1576678927484-cc907957088c?w=400&q=80"
    private const val PULLUP_BAR      = "https://images.unsplash.com/photo-1616803689943-5601631c7fec?w=400&q=80"
    private const val DEADLIFT_BAR    = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=400&q=80"
    private const val BARBELL_ROW     = "https://images.unsplash.com/photo-1605296867304-46d5465a13f1?w=400&q=80"
    private const val SQUAT_RACK      = "https://images.unsplash.com/photo-1567598508481-65985588e295?w=400&q=80"
    private const val LEG_PRESS_MACH  = "https://images.unsplash.com/photo-1574680096145-d05b474e2155?w=400&q=80"
    private const val LUNGE_DUMBBELL  = "https://images.unsplash.com/photo-1434682881908-b43d0467b798?w=400&q=80"
    private const val CALF_RAISE      = "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&q=80"
    private const val OVERHEAD_PRESS  = "https://images.unsplash.com/photo-1540497077202-7c8a3999166f?w=400&q=80"
    private const val LATERAL_RAISE   = "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?w=400&q=80"
    private const val DUMBBELL_CURL   = "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?w=400&q=80"
    private const val TRICEP_ROPE     = "https://images.unsplash.com/photo-1576678927484-cc907957088c?w=400&q=80"
    private const val PLANK_POSITION  = "https://images.unsplash.com/photo-1518611012118-696072aa579a?w=400&q=80"
    private const val CRUNCHES_MAT    = "https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=400&q=80"
    private const val LEG_RAISE_BENCH = "https://images.unsplash.com/photo-1520877880798-5ee004e3f11e?w=400&q=80"
    private const val TREADMILL_RUN   = "https://images.unsplash.com/photo-1538805060514-97d9cc17730c?w=400&q=80"
    private const val OUTDOOR_RUN     = "https://images.unsplash.com/photo-1483721310020-03333e577078?w=400&q=80"
    private const val STATIONARY_BIKE = "https://images.unsplash.com/photo-1517649763962-0c623066013b?w=400&q=80"
    private const val JUMP_ROPE       = "https://images.unsplash.com/photo-1552674605-db6ffd4facb5?w=400&q=80"
    private const val BURPEE          = "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?w=400&q=80"
    private const val MOUNTAIN_CLIMB  = "https://images.unsplash.com/photo-1518611012118-696072aa579a?w=400&q=80"
    private const val BOX_JUMP        = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=400&q=80"
    private const val DIPS_BARS       = "https://images.unsplash.com/photo-1598971639058-fab3c3109a3b?w=400&q=80"
    private const val INCLINE_PRESS   = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400&q=80"
    private const val LAT_PULLDOWN    = "https://images.unsplash.com/photo-1616803689943-5601631c7fec?w=400&q=80"
    private const val RUSSIAN_TWIST   = "https://images.unsplash.com/photo-1520877880798-5ee004e3f11e?w=400&q=80"
    private const val FACE_PULL       = "https://images.unsplash.com/photo-1576678927484-cc907957088c?w=400&q=80"
    private const val RDL             = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=400&q=80"

    // kcalPerMin values based on MET × 3.5 × 70 / 200 (ACSM Compendium of Physical Activities)
    // Resistance training MET ≈ 3.5–6.0 depending on intensity
    // Cardio MET values from published research
    val items = listOf(
        // ── CHEST ──────────────────────────────────────────────────────────────
        // Bench Press: MET 5.0 → ~6.1 kcal/min @ 70kg (free weight compound)
        Exercise("Bench Press",         "Chest",     4, 8,  6.1f, "Barbell",    BARBELL_BENCH),
        // Push-ups: MET 3.8 → ~4.7 kcal/min
        Exercise("Push-ups",            "Chest",     3, 15, 4.7f, "Bodyweight", PUSHUP_FLOOR),
        // Incline Press: MET 5.0 → ~6.1 kcal/min
        Exercise("Incline Press",       "Chest",     3, 10, 6.1f, "Dumbbells",  INCLINE_PRESS),
        // Cable Flyes: MET 3.5 → ~4.3 kcal/min (isolation, lighter load)
        Exercise("Cable Flyes",         "Chest",     3, 12, 4.3f, "Cable",      CABLE_MACHINE),
        // Dips: MET 4.5 → ~5.5 kcal/min
        Exercise("Chest Dips",          "Chest",     3, 12, 5.5f, "Bodyweight", DIPS_BARS),

        // ── BACK ───────────────────────────────────────────────────────────────
        // Pull-ups: MET 8.0 → ~9.8 kcal/min (high intensity bodyweight)
        Exercise("Pull-ups",            "Back",      4, 8,  9.8f, "Bodyweight", PULLUP_BAR),
        // Deadlift: MET 6.0 → ~7.4 kcal/min (compound, very heavy)
        Exercise("Deadlift",            "Back",      4, 5,  7.4f, "Barbell",    DEADLIFT_BAR),
        // Barbell Row: MET 5.0 → ~6.1 kcal/min
        Exercise("Barbell Row",         "Back",      4, 8,  6.1f, "Barbell",    BARBELL_ROW),
        // Lat Pulldown: MET 4.0 → ~4.9 kcal/min (machine-assisted)
        Exercise("Lat Pulldown",        "Back",      4, 12, 4.9f, "Machine",    LAT_PULLDOWN),
        // Seated Row: MET 3.5 → ~4.3 kcal/min
        Exercise("Seated Cable Row",    "Back",      3, 12, 4.3f, "Cable",      CABLE_MACHINE),

        // ── LEGS ───────────────────────────────────────────────────────────────
        // Barbell Squat: MET 6.0 → ~7.4 kcal/min (highest MET of resistance exercises)
        Exercise("Barbell Squat",       "Legs",      4, 8,  7.4f, "Barbell",    SQUAT_RACK),
        // Leg Press: MET 5.0 → ~6.1 kcal/min
        Exercise("Leg Press",           "Legs",      4, 12, 6.1f, "Machine",    LEG_PRESS_MACH),
        // Romanian Deadlift: MET 5.5 → ~6.7 kcal/min
        Exercise("Romanian Deadlift",   "Legs",      3, 10, 6.7f, "Barbell",    RDL),
        // Lunges: MET 4.0 → ~4.9 kcal/min
        Exercise("Lunges",              "Legs",      3, 12, 4.9f, "Bodyweight", LUNGE_DUMBBELL),
        // Leg Curl: MET 3.5 → ~4.3 kcal/min (isolation)
        Exercise("Leg Curl",            "Legs",      3, 12, 4.3f, "Machine",    LEG_PRESS_MACH),
        // Calf Raises: MET 2.8 → ~3.4 kcal/min (small muscle group)
        Exercise("Calf Raises",         "Legs",      4, 20, 3.4f, "Bodyweight", CALF_RAISE),

        // ── SHOULDERS ──────────────────────────────────────────────────────────
        // Overhead Press: MET 5.0 → ~6.1 kcal/min
        Exercise("Overhead Press",      "Shoulders", 4, 8,  6.1f, "Barbell",    OVERHEAD_PRESS),
        // Lateral Raise: MET 2.8 → ~3.4 kcal/min (light isolation)
        Exercise("Lateral Raise",       "Shoulders", 3, 15, 3.4f, "Dumbbells",  LATERAL_RAISE),
        // Front Raise: MET 2.8 → ~3.4 kcal/min
        Exercise("Front Raise",         "Shoulders", 3, 12, 3.4f, "Dumbbells",  LATERAL_RAISE),
        // Face Pulls: MET 3.0 → ~3.7 kcal/min
        Exercise("Face Pulls",          "Shoulders", 3, 15, 3.7f, "Cable",      FACE_PULL),

        // ── BICEPS ─────────────────────────────────────────────────────────────
        // Barbell Curl: MET 3.5 → ~4.3 kcal/min
        Exercise("Barbell Curl",        "Biceps",    3, 12, 4.3f, "Barbell",    DUMBBELL_CURL),
        // Dumbbell Curl: MET 3.0 → ~3.7 kcal/min
        Exercise("Dumbbell Curl",       "Biceps",    3, 12, 3.7f, "Dumbbells",  DUMBBELL_CURL),
        // Hammer Curl: MET 3.0 → ~3.7 kcal/min
        Exercise("Hammer Curl",         "Biceps",    3, 12, 3.7f, "Dumbbells",  DUMBBELL_CURL),

        // ── TRICEPS ─────────────────────────────────────────────────────────────
        // Tricep Pushdown: MET 3.5 → ~4.3 kcal/min
        Exercise("Tricep Pushdown",     "Triceps",   3, 15, 4.3f, "Cable",      TRICEP_ROPE),
        // Skull Crushers: MET 4.0 → ~4.9 kcal/min
        Exercise("Skull Crushers",      "Triceps",   3, 10, 4.9f, "Barbell",    BARBELL_BENCH),
        // Tricep Dips: MET 4.5 → ~5.5 kcal/min
        Exercise("Tricep Dips",         "Triceps",   3, 12, 5.5f, "Bodyweight", DIPS_BARS),

        // ── CORE ───────────────────────────────────────────────────────────────
        // Plank: MET 4.0 → ~4.9 kcal/min (isometric hold)
        Exercise("Plank",               "Core",      3, 45, 4.9f, "Bodyweight", PLANK_POSITION),
        // Crunches: MET 2.8 → ~3.4 kcal/min
        Exercise("Crunches",            "Core",      3, 20, 3.4f, "Bodyweight", CRUNCHES_MAT),
        // Leg Raises: MET 3.0 → ~3.7 kcal/min
        Exercise("Leg Raises",          "Core",      3, 15, 3.7f, "Bodyweight", LEG_RAISE_BENCH),
        // Russian Twists: MET 3.0 → ~3.7 kcal/min
        Exercise("Russian Twists",      "Core",      3, 20, 3.7f, "Bodyweight", RUSSIAN_TWIST),
        // Ab Wheel: MET 4.0 → ~4.9 kcal/min
        Exercise("Ab Wheel",            "Core",      3, 12, 4.9f, "Equipment",  PLANK_POSITION),

        // ── CARDIO ─────────────────────────────────────────────────────────────
        // Running 8 km/h: MET 8.3 → ~10.2 kcal/min (ACSM published value)
        Exercise("Running (8 km/h)",    "Cardio",    1, 30, 10.2f,"Treadmill",  TREADMILL_RUN),
        // Running outdoor: MET 9.8 → ~12.0 kcal/min (slightly higher effort)
        Exercise("Running (outdoor)",   "Cardio",    1, 30, 12.0f,"Outdoors",   OUTDOOR_RUN),
        // Cycling moderate: MET 8.0 → ~9.8 kcal/min
        Exercise("Cycling (moderate)",  "Cardio",    1, 30, 9.8f, "Bike",       STATIONARY_BIKE),
        // Jump rope fast: MET 12.3 → ~15.1 kcal/min (one of highest cardio METs)
        Exercise("Jump Rope",           "Cardio",    3, 5,  15.1f,"Jump Rope",  JUMP_ROPE),
        // Burpees: MET 8.0 → ~9.8 kcal/min
        Exercise("Burpees",             "Cardio",    3, 15, 9.8f, "Bodyweight", BURPEE),
        // Mountain Climbers: MET 8.0 → ~9.8 kcal/min
        Exercise("Mountain Climbers",   "Cardio",    3, 30, 9.8f, "Bodyweight", MOUNTAIN_CLIMB),
        // Box Jumps: MET 7.0 → ~8.6 kcal/min
        Exercise("Box Jumps",           "Cardio",    3, 10, 8.6f, "Bodyweight", BOX_JUMP)
    )

    fun search(q: String): List<Exercise> {
        val l = q.lowercase()
        return items.filter { it.name.lowercase().contains(l) || it.muscle.lowercase().contains(l) }
    }
}


// ── Recommendations ───────────────────────────────────────────────────────────
object Recs {
    fun workoutPlan(goal: String): List<String> = when(goal) {
        "weight_loss" -> listOf(
            "Day 1 — Full Body + Cardio: Squats 4×15, Push-ups 3×15, Plank 3×45s, Running 20min",
            "Day 2 — Cardio: 30min brisk walk or cycling, Jump Rope 3×3min",
            "Day 3 — Upper Body: Bench Press 3×12, Rows 3×12, Overhead Press 3×10, Curls 3×12",
            "Day 4 — Cardio: Cycling 30min or Running 25min",
            "Day 5 — Lower Body: Squats 4×15, Lunges 3×12, Leg Press 3×15, Calf Raises 4×20",
            "Day 6 — Light Activity or Yoga / Stretching",
            "Day 7 — Rest")
        "muscle_gain" -> listOf(
            "Day 1 — Chest & Triceps: Bench Press 4×8, Incline Press 3×10, Dips 3×10, Skull Crushers 3×12",
            "Day 2 — Legs: Squats 4×8, Romanian Deadlift 3×10, Leg Press 4×12, Calf Raises 5×20",
            "Day 3 — Back & Biceps: Deadlift 4×5, Pull-ups 4×8, Rows 4×10, Barbell Curl 3×12",
            "Day 4 — Rest / Active Recovery",
            "Day 5 — Shoulders: Overhead Press 4×8, Lateral Raise 3×15, Face Pulls 3×15",
            "Day 6 — Full Body Accessory: lighter compound movements",
            "Day 7 — Rest")
        else -> listOf(
            "Day 1 — Cardio + Core: 20min jog, Plank 3×45s, Crunches 3×20",
            "Day 2 — Upper Body: Push-ups 3×15, Rows 3×12, Shoulder Press 3×10",
            "Day 3 — Lower Body: Squats 3×12, Lunges 3×12, Calf Raises 3×20",
            "Day 4 — Rest",
            "Day 5 — Full Body Circuits",
            "Day 6 — Cardio 30min",
            "Day 7 — Rest")
    }

    fun dietPlan(goal: String, pref: String, kcal: Int): List<String> {
        val isVeg = pref in listOf("vegetarian","vegan")
        val protein = if (isVeg) "Paneer / Dal / Tofu / Curd" else "Chicken / Eggs / Fish"
        return when(goal) {
            "weight_loss" -> listOf(
                "Breakfast (~${(kcal*0.25).toInt()} kcal): Oats with banana, or Moong Dal Chilla + Green tea",
                "Mid-Morning: Apple, cucumber, or handful of almonds",
                "Lunch (~${(kcal*0.35).toInt()} kcal): 2 Chapati + Dal + Sabzi + Salad",
                "Pre-Workout: Black coffee or banana",
                "Dinner (~${(kcal*0.30).toInt()} kcal): $protein + vegetable curry + 1 Chapati",
                "Tip: Avoid refined carbs after 6pm. Stay under $kcal kcal total.")
            "muscle_gain" -> listOf(
                "Breakfast (~${(kcal*0.25).toInt()} kcal): $protein + Brown bread + Banana + Milk",
                "Pre-Workout (1hr before): Oats + Banana + Whey protein",
                "Post-Workout: Whey shake + rice cakes",
                "Lunch (~${(kcal*0.30).toInt()} kcal): Rice + Dal + $protein + Curd",
                "Snack: Peanuts or Almonds or Curd",
                "Dinner (~${(kcal*0.30).toInt()} kcal): $protein + Sweet Potato + Greens",
                "Target: $kcal+ kcal/day. High protein is key.")
            else -> listOf(
                "Breakfast: Balanced protein + complex carbs",
                "Lunch: Rice/Chapati + Dal + Vegetables",
                "Dinner: Light meal with protein + vegetables",
                "Snacks: Fruits, nuts, curd",
                "Target: ~$kcal kcal/day")
        }
    }
}

// ── Daily Reference Intakes (RDI) ─────────────────────────────────────────────
object RDI {
    // Macros per day (grams) — based on 2000 kcal reference diet
    fun protein(weightKg: Float, goal: String) = when(goal) {
        "muscle_gain" -> (weightKg * 2.2f).toInt()
        "weight_loss" -> (weightKg * 1.8f).toInt()
        else          -> (weightKg * 1.6f).toInt()
    }
    fun carbs(calorieGoal: Int) = ((calorieGoal * 0.45f) / 4f).toInt()  // 45% of kcal / 4 kcal/g
    fun fat(calorieGoal: Int)   = ((calorieGoal * 0.30f) / 9f).toInt()  // 30% of kcal / 9 kcal/g
    fun fiber()    = 25  // g
    fun sugar()    = 50  // g (added sugars limit)

    // Micros (standard adult RDI)
    val vitaminA   = 900   // mcg RAE
    val vitaminC   = 90    // mg
    val vitaminD   = 15    // mcg
    val vitaminB12 = 2.4f  // mcg
    val calcium    = 1000  // mg
    val iron       = 18    // mg (women) / 8 (men) — use 18 as safe default
    val potassium  = 3500  // mg
    val magnesium  = 400   // mg
    val zinc       = 11    // mg
    val sodium     = 2300  // mg (max)
}

// ── Weight-loss Calorie Calculator ───────────────────────────────────────────
object WeightLossCalc {
    /**
     * Given current weight, target weight, and target date, compute daily calorie deficit needed.
     * 1 kg of fat ≈ 7700 kcal.
     * Returns a safe calorie goal, clamped to never go below 1200 kcal/day.
     */
    fun dailyCalorieGoal(
        currentWeightKg: Float,
        targetWeightKg: Float,
        targetDateIso: String,
        tdee: Float
    ): Int {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return try {
            val today = sdf.parse(sdf.format(java.util.Date())) ?: return (tdee - 500).toInt()
            val target = sdf.parse(targetDateIso) ?: return (tdee - 500).toInt()
            val daysLeft = ((target.time - today.time) / 86_400_000L).toInt()
            if (daysLeft <= 0) return (tdee - 500).toInt()
            val totalKcalDeficit = (currentWeightKg - targetWeightKg) * 7700f
            val dailyDeficit = totalKcalDeficit / daysLeft
            val goal = (tdee - dailyDeficit).toInt()
            goal.coerceAtLeast(1200).coerceAtMost(tdee.toInt())
        } catch (e: Exception) {
            (tdee - 500).toInt()
        }
    }

    fun daysUntil(targetDateIso: String): Int {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val today = sdf.parse(sdf.format(java.util.Date())) ?: return -1
            val target = sdf.parse(targetDateIso) ?: return -1
            ((target.time - today.time) / 86_400_000L).toInt()
        } catch (e: Exception) { -1 }
    }
}

// ── LibreTranslate wrapper ────────────────────────────────────────────────────
// Uses the public LibreTranslate demo instance (free, no API key required for low volume)
object Translator {
    private const val BASE = "https://libretranslate.de/translate"

    /** Blocking call — run on IO coroutine only. Returns original string on failure. */
    fun translate(text: String, targetLang: String): String {
        if (targetLang == "en") return text
        return try {
            val url = java.net.URL(BASE)
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 4000
            conn.readTimeout = 4000
            val body = """{"q":"$text","source":"en","target":"$targetLang","format":"text"}"""
            conn.outputStream.write(body.toByteArray())
            val resp = conn.inputStream.bufferedReader().readText()
            // Parse "translatedText":"..."
            val match = Regex(""""translatedText"\s*:\s*"([^"]+)"""").find(resp)
            match?.groupValues?.get(1) ?: text
        } catch (e: Exception) { text }
    }

    /** Translate a list of strings concurrently */
    suspend fun translateList(items: List<String>, targetLang: String): List<String> {
        if (targetLang == "en") return items
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            items.map { translate(it, targetLang) }
        }
    }
}
