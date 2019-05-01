package taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> {
    val realDrivers = trips.map { trip -> trip.driver }.toSet()
    return allDrivers.filterNot { driver -> realDrivers.contains(driver) }.toSet()
}

fun TaxiPark.findFakeDriversCourseraSolution(): Set<Driver> = allDrivers - trips.map { it.driver }


/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> {
    val passengersInTrips = trips.flatMap(Trip::passengers)
    return allPassengers.filter { passenger -> passengersInTrips.count { it == passenger } >= minTrips }.toSet()
}

fun TaxiPark.findFaithfulPassengersCourseraSolution(minTrips: Int): Set<Passenger> =
        trips
                .flatMap(Trip::passengers)
                .groupBy { passenger -> passenger }
                .filterValues { group -> group.size > minTrips }
                .keys

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> {
    var driverTrips = trips.filter { it.driver == driver }.flatMap { it.passengers }
    return driverTrips.filter { passenger -> driverTrips.count { it == passenger } > 1 }.toSet()
}

fun TaxiPark.findFrequentPassengersCourseraSolution(driver: Driver): Set<Passenger> =
        allPassengers.filter { p -> trips.count { it.driver == driver && p in it.passengers } > 1 }.toSet()


/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> {
    val getPassengerTrips: (Passenger) -> List<Trip> = { passenger -> trips.filter { it.passengers.contains(passenger) } }
    val getDiscountedTrips: (List<Trip>) -> List<Trip> = { allPassengerTrips -> allPassengerTrips.filter { it.discount ?: 0.0 > 0.0 } }
    val allTripsPassengers = trips.flatMap { it.passengers }.toSet()

    fun getSmartPassenger(passenger: Passenger): Boolean {
        val passengerTrips = getPassengerTrips(passenger)
        val discountedTrips = getDiscountedTrips(passengerTrips)
        return discountedTrips.size > passengerTrips.size - discountedTrips.size
    }
    return allTripsPassengers.filter { getSmartPassenger(it) }.toSet()
}

fun TaxiPark.findSmartPassengersCourseraSolution(): Set<Passenger> =
        allPassengers
                .associate { p -> p to trips.filter { t -> p in t.passengers } }
                .filterValues { passengerTrips ->
                    val (withDiscount, withoutDiscount) = passengerTrips.partition { it.discount != null }
                    withDiscount.size > withoutDiscount.size
                }.keys

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? =
        trips.map { (it.duration / 10) * 10..(it.duration / 10) * 10 + 9 }.toSet()
                .map { durationRange -> Pair(durationRange, trips.count { it.duration in durationRange }) }.maxBy { it.second }?.first

fun TaxiPark.findTheMostFrequentTripDurationPeriodCourseraSolution(): IntRange? =
        trips.groupBy {
            val start = it.duration / 10 * 10
            val end = start + 9
            start..end
        }.maxBy { (_, group) -> group.size }?.key


/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    if (trips.isEmpty()) return false
    val allIncome = trips.sumByDouble { it.cost }
    val sortedDriversPair = allDrivers.map { driver -> Pair(driver, trips.filter { it.driver == driver }.sumByDouble { it.cost }) }.sortedByDescending { it.second }
    val topDriversCount = (allDrivers.size * 0.2).toInt()
    return sortedDriversPair.take(topDriversCount).sumByDouble { it.second } >= allIncome * 0.8
}