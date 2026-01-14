package ru.webrelab.kie.cerealstorage

class CerealStorageImpl(
    override val containerCapacity: Float,
    override val storageCapacity: Float
) : CerealStorage {

    /**
     * Блок инициализации класса.
     * Выполняется сразу при создании объекта
     */
    init {
        require(containerCapacity >= 0) {
            "Ёмкость контейнера не может быть отрицательной"
        }
        require(storageCapacity >= containerCapacity) {
            "Ёмкость хранилища не должна быть меньше ёмкости одного контейнера"
        }
    }

    private val storage = mutableMapOf<Cereal, Float>()

    /**
     * Добавляет крупу к существующему контейнеру соответствующего типа, либо добавляет новый контейнер
     * если его ещё не было в хранилище и добавляет в него предоставленную крупу.
     * Для одного вида крупы только один контейнер.
     * @param cereal крупа для добавления в контейнер
     * @param amount количество добавляемой крупы
     * @return количество оставшейся крупы если контейнер заполнился
     * @throws IllegalArgumentException если передано отрицательное значение
     * @throws IllegalStateException если хранилище не позволяет разместить ещё один контейнер для новой крупы
     */
    override fun addCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0f) { "Количество добавляемой крупы не может быть отрицательным" }

        if (!storage.contains(cereal)) {
            check(hasFreeContainerSpace()) {
                "Контейнер не помещается в хранилище"
            }
        }

        val currentAmount = getAmount(cereal)
        return fillContainer(cereal, currentAmount, amount)
    }

    private fun hasFreeContainerSpace(): Boolean {
        val maxContainers = (storageCapacity / containerCapacity).toInt()
        val usedContainers = storage.size
        val freeContainers = maxContainers - usedContainers
        return freeContainers > 0
    }

    private fun fillContainer(
        cereal: Cereal,
        current: Float,
        amount: Float
    ): Float {
        val totalAmount = current + amount
        if (totalAmount <= containerCapacity) {
            storage[cereal] = totalAmount
            return 0f
        } else {
            storage[cereal] = containerCapacity
            return totalAmount - containerCapacity
        }
    }

    /**
     * Вынимает крупу из контейнера (после этого в контейнере крупы должно стать меньше)
     * @param cereal крупа, которую нужно взять из контейнера
     * @param amount количество крупы
     * @return количество полученной крупы или остаток содержимого контейнера, если крупы в нём было меньше
     * @throws IllegalArgumentException если передано отрицательное значение
     */
    override fun getCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0f) { "Количество вынимаемой крупы не может быть отрицательным" }

        val currentAmount = getAmount(cereal)
        var takenAmount = 0f
        if (currentAmount >= amount) {
            takenAmount = amount
            storage[cereal] = currentAmount - amount
        } else {
            takenAmount = currentAmount
            storage[cereal] = 0f
        }
        return takenAmount
    }

    /**
     * @param cereal уничтожает пустой контейнер
     * @return true если контейнер уничтожен и false если контейнер не пуст или отсутствует
     */
    override fun removeContainer(cereal: Cereal): Boolean {
        return if (storage.contains(cereal) && getAmount(cereal) == 0f) {
            storage.remove(cereal)
            true
        } else false
    }

    /**
     * @param cereal крупа, количество которой нужно узнать
     * @return количество крупы, которое хранится в контейнере или 0 если контейнера нет
     */
    override fun getAmount(cereal: Cereal) = storage.getOrDefault(cereal, 0f)

    /**
     * @param cereal крупа, для которой нужно проверить доступное место в контейнере
     * @return количество крупы, которое может вместить контейнер с учётом его текущей заполненности
     * @throws IllegalStateException если проверяемого контейнера нет
     */
    override fun getSpace(cereal: Cereal): Float {
        check(storage.contains(cereal)) { "Запрашиваемого контейнера нет в хранилище" }
        return containerCapacity - getAmount(cereal)
    }

    /**
     * @return текстовое представление
     */
    override fun toString(): String {
        return if (storage.isEmpty()) "Хранилище не содержит контейнеров"
        else storage.entries
            .joinToString("\n") { "Крупа: ${it.key.local}, количество: ${it.value}" }
    }
}
