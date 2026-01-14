package ru.webrelab.kie.cerealstorage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class CerealStorageImplTest {

    private lateinit var storage: CerealStorageImpl

    @BeforeEach
    fun setUp() {
        storage = CerealStorageImpl(10f, 25f)
    }

    @Test
    fun `CerealStorageImpl throws if containerCapacity is negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            CerealStorageImpl(-4f, 10f)
        }
    }

    @Test
    fun `CerealStorageImpl throws if containerCapacity is larger than storageCapacity`() {
        assertThrows(IllegalArgumentException::class.java) {
            CerealStorageImpl(10f, 5f)
        }
    }

    @ParameterizedTest
    @MethodSource("provideNewCerealsAndAmounts")
    fun `addCereal adds and creates new container`(
        cereal: Cereal,
        amount: Float
    ) {
        assertEquals(0f, storage.addCereal(cereal, amount), 0.01f)
    }

    @ParameterizedTest
    @MethodSource("provideCerealsAndAmountsForExistedContainers")
    fun `addCereal adds in exist container and returns amount of remaining cereal`(
        cereal: Cereal,
        firstAmount: Float,
        secondAmount: Float,
        expectedResult: Float
    ) {
        storage.addCereal(cereal, firstAmount)
        assertEquals(expectedResult, storage.addCereal(cereal, secondAmount), 0.01f)
    }

    @Test
    fun `addCereal throws if amount of cereal is negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            storage.addCereal(Cereal.BUCKWHEAT, -1f)
        }
    }

    @Test
    fun `addCereal throws if storageCapacity is full`() {
        storage.addCereal(Cereal.BUCKWHEAT, 10f)
        storage.addCereal(Cereal.RICE, 7f)
        assertThrows(IllegalStateException::class.java) {
            storage.addCereal(Cereal.BULGUR, 6f)
        }
    }

    @ParameterizedTest
    @MethodSource("provideCerealsAndAmountToGet")
    fun `getCereal takes amount of cereal out of container`(
        cereal: Cereal,
        amountToAdd: Float,
        amountToGet: Float,
        expectedResult: Float
    ) {
        storage.addCereal(cereal, amountToAdd)
        assertEquals(expectedResult, storage.getCereal(cereal, amountToGet), 0.01f)
    }

    @Test
    fun `getCereal throws if amount of cereal to take out is negative`() {
        storage.addCereal(Cereal.BUCKWHEAT, 4f)
        assertThrows(IllegalArgumentException::class.java) {
            storage.getCereal(Cereal.BUCKWHEAT, -1f)
        }
    }

    @Test
    fun `removeContainer removes empty container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 0f)
        assertTrue { storage.removeContainer(Cereal.BUCKWHEAT) }
    }

    @Test
    fun `removeContainer does not remove not empty container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        assertFalse { storage.removeContainer(Cereal.BUCKWHEAT) }
    }

    @Test
    fun `removeContainer does not remove not exist container`() {
        assertFalse { storage.removeContainer(Cereal.BUCKWHEAT) }
    }

    @Test
    fun `getAmount returns amount of cereal`() {
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        assertEquals(5f, storage.getAmount(Cereal.BUCKWHEAT))
    }

    @Test
    fun `getAmount returns zero amount of cereal if container doesn't exist`() {
        assertEquals(0f, storage.getAmount(Cereal.BUCKWHEAT))
    }

    @Test
    fun `getSpace returns available amount of space for cereal in the container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 4f)
        assertEquals(6f, storage.getSpace(Cereal.BUCKWHEAT))
    }

    @Test
    fun `getSpace throws if container doesn't exist`() {
        assertThrows(IllegalStateException::class.java) {
            storage.getSpace(Cereal.BUCKWHEAT)
        }
    }

    @Test
    fun `toString transforms to text storage with container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 4f)
        assertEquals("Крупа: Гречка, количество: 4.0", storage.toString())
    }

    @Test
    fun `toString transforms to text empty storage`() {
        assertEquals("Хранилище не содержит контейнеров", storage.toString())
    }

    companion object {
        @JvmStatic
        fun provideNewCerealsAndAmounts() = listOf(
            arrayOf(Cereal.BUCKWHEAT, 0f),
            arrayOf(Cereal.RICE, 0.1f),
            arrayOf(Cereal.MILLET, 9f),
            arrayOf(Cereal.PEAS, 9.9f),
            arrayOf(Cereal.BULGUR, 10f)
        )

        @JvmStatic
        fun provideCerealsAndAmountsForExistedContainers() = listOf(
            arrayOf(Cereal.BUCKWHEAT, 0f, 0f, 0f),
            arrayOf(Cereal.RICE, 3.5f, 2.0f, 0f),
            arrayOf(Cereal.MILLET, 5f, 6f, 1f),
            arrayOf(Cereal.PEAS, 9.1f, 0.9f, 0f),
            arrayOf(Cereal.BULGUR, 10f, 0.1f, 0.1f)
        )

        @JvmStatic
        fun provideCerealsAndAmountToGet() = listOf(
            arrayOf(Cereal.BUCKWHEAT, 10f, 6f, 6f),
            arrayOf(Cereal.RICE, 4f, 6f, 4f),
            arrayOf(Cereal.MILLET, 9.1f, 11f, 9.1f),
            arrayOf(Cereal.PEAS, 5f, 4f, 4f),
            arrayOf(Cereal.BULGUR, 0.1f, 0.1f, 0.1f),
        )
    }
}
