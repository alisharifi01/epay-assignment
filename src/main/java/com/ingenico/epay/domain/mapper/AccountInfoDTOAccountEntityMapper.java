package com.ingenico.epay.domain.mapper;

import com.ingenico.epay.api.dto.AccountInfoDTO;
import com.ingenico.epay.domain.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountInfoDTOAccountEntityMapper {

    AccountInfoDTOAccountEntityMapper INSTANCE = Mappers.getMapper(AccountInfoDTOAccountEntityMapper.class);

    @Mappings(
           @Mapping(source = "initBalance" , target = "balance")
    )
    Account toAccountEntity(AccountInfoDTO accountInfoDTO);
}
