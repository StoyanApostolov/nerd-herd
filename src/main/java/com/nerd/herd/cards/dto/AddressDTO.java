package com.nerd.herd.cards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nerd.herd.util.ReflectionUtils;
import com.nerd.herd.cards.domain.HyperAddress;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AddressDTO extends HyperAddress {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Boolean defaultAddress;

	public AddressDTO(HyperAddress address) {
		ReflectionUtils.copyNonNullProps(address, this);
	}

	public HyperAddress toHyperAddress() {
		final HyperAddress hyperAddress = new HyperAddress();
		ReflectionUtils.copyNonNullProps(this, hyperAddress);
		return hyperAddress;
	}

	public AddressDTO markDefaultIfMatches(final String defaultId) {
		defaultAddress = defaultId.equals(getId());
		return this;
	}
}
