package br.com.edumatt3.cars

import br.com.edumatt3.CarsGrpcRequest
import br.com.edumatt3.CarsGrpcServiceGrpc
import io.grpc.Channel
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest(transactional = false) //sem transação porque o servidor grpc roda em uma thread separada
internal class CarsEnpointTest(
    private val carRepository: CarRepository,
    private val grpcClient: CarsGrpcServiceGrpc.CarsGrpcServiceBlockingStub
) {

    /*
    * 1. happy path
    * 2. quando a placa já existe
    * 3. quando os dados de entrada são inválidos
    * */
    @BeforeEach
    internal fun setUp() {
        carRepository.deleteAll()
    }

    //deve/não deve template

    //esse test tem um effeito colateral: persiste dados em disco
    @Test
    fun `deve adicionar um carro novo`(){
        //para um cliente é preciso criar uma fabrica e prover um client
        val response = grpcClient.add(CarsGrpcRequest.newBuilder()
            .setModel("Uno")
            .setLicensePlate("XXX-0000")
            .build())

        with(response){
            assertNotNull(id)
            assertTrue(carRepository.existsById(id)) //validando efeito colaterla, se a integração realmente está rolando
        }
    }

    @Test
    internal fun `nao deve adicionar carro com placa existente`() {

        val licensePlate = "PWM-9999"
        carRepository.save(Car("Uno", licensePlate))

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.add(
                CarsGrpcRequest.newBuilder()
                    .setModel("Fusqueta")
                    .setLicensePlate(licensePlate)
                    .build()
            )
        }

        with(error){
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("license plate already exists", status.description)
        }

    }

    @Test
    internal fun `nao deve add carro quando dados de entrada forem invalidos`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.add(CarsGrpcRequest.newBuilder().build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("inválid entry data", status.description)
        }
    }

    @Factory
    class Clients {

        @Singleton
        //porta aleatória, para pegar: GrpcServerChannel.NAME
        fun blockStub(@GrpcChannel(GrpcServerChannel.NAME) channel: Channel): CarsGrpcServiceGrpc.CarsGrpcServiceBlockingStub? {
            return CarsGrpcServiceGrpc.newBlockingStub(channel)
        }
    }


}