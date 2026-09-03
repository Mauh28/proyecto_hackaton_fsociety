package com.hackaton.prog.model.converter;

import com.hackaton.prog.model.enums.*;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public class EnumsConverters {

    @Converter(autoApply = true)
    public static class RolUsuarioConverter implements AttributeConverter<RolUsuario, String> {
        @Override
        public String convertToDatabaseColumn(RolUsuario attribute) {
            return attribute != null ? attribute.name() : null;
        }

        @Override
        public RolUsuario convertToEntityAttribute(String dbData) {
            return dbData != null ? RolUsuario.desdeValorDb(dbData) : null;
        }
    }

    @Converter(autoApply = true)
    public static class CategoriaArticuloConverter implements AttributeConverter<CategoriaArticulo, String> {
        @Override
        public String convertToDatabaseColumn(CategoriaArticulo attribute) {
            return attribute != null ? attribute.name() : null;
        }

        @Override
        public CategoriaArticulo convertToEntityAttribute(String dbData) {
            return dbData != null ? CategoriaArticulo.desdeValorDb(dbData) : null;
        }
    }

    @Converter(autoApply = true)
    public static class UnidadMedidaConverter implements AttributeConverter<UnidadMedida, String> {
        @Override
        public String convertToDatabaseColumn(UnidadMedida attribute) {
            return attribute != null ? attribute.name() : null;
        }

        @Override
        public UnidadMedida convertToEntityAttribute(String dbData) {
            return dbData != null ? UnidadMedida.desdeValorDb(dbData) : null;
        }
    }

    @Converter(autoApply = true)
    public static class EstadoTransferenciaConverter implements AttributeConverter<EstadoTransferencia, String> {
        @Override
        public String convertToDatabaseColumn(EstadoTransferencia attribute) {
            return attribute != null ? attribute.name() : null;
        }

        @Override
        public EstadoTransferencia convertToEntityAttribute(String dbData) {
            return dbData != null ? EstadoTransferencia.desdeValorDb(dbData) : null;
        }
    }

    @Converter(autoApply = true)
    public static class TipoMovimientoConverter implements AttributeConverter<TipoMovimiento, String> {
        @Override
        public String convertToDatabaseColumn(TipoMovimiento attribute) {
            return attribute != null ? attribute.name() : null;
        }

        @Override
        public TipoMovimiento convertToEntityAttribute(String dbData) {
            return dbData != null ? TipoMovimiento.desdeValorDb(dbData) : null;
        }
    }

    @Converter(autoApply = true)
    public static class MotivoMovimientoConverter implements AttributeConverter<MotivoMovimiento, String> {
        @Override
        public String convertToDatabaseColumn(MotivoMovimiento attribute) {
            return attribute != null ? attribute.name() : null;
        }

        @Override
        public MotivoMovimiento convertToEntityAttribute(String dbData) {
            return dbData != null ? MotivoMovimiento.desdeValorDb(dbData) : null;
        }
    }
}
