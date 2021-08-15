package br.com.edumatt3.cars

import br.com.edumatt3.CarsGrpcReply
import br.com.edumatt3.CarsGrpcRequest
import br.com.edumatt3.CarsGrpcServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class CarsEnpoint(private val carRepository: CarRepository) : CarsGrpcServiceGrpc.CarsGrpcServiceImplBase() {

    override fun add(request: CarsGrpcRequest?, responseObserver: StreamObserver<CarsGrpcReply>?) {

        if (carRepository.existsByLicensePlate(request?.licensePlate)){

            responseObserver?.onError(Status.ALREADY_EXISTS
                .withDescription("license plate already exists")
                .asRuntimeException())
            return
        }

        val car = Car(request!!.model, request.licensePlate)

        try {
            carRepository.save(car)
        } catch (e: ConstraintViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT
                .withDescription("inválid entry data")
                .asRuntimeException())
            return
        }

        responseObserver?.onNext(CarsGrpcReply.newBuilder().setId(car.id!!).build())
        responseObserver?.onCompleted()
    }
}