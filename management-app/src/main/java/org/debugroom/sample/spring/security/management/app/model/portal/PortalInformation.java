package org.debugroom.sample.spring.security.management.app.model.portal;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.debugroom.sample.spring.security.common.model.user.UserResource;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PortalInformation implements Serializable {

    UserResource userResource;

}
