package com.ingenico.epay.domain.mapper;

import com.ingenico.epay.api.dto.AccountInfoDTO;
import com.ingenico.epay.domain.Party;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountInfoDTOPartyEntityMapper {

    AccountInfoDTOPartyEntityMapper INSTANCE = Mappers.getMapper(AccountInfoDTOPartyEntityMapper.class);

    @Mappings(
            @Mapping(source = "ownerName" , target = "name")
    )
    Party toParty(AccountInfoDTO accountInfoDTO);
}
